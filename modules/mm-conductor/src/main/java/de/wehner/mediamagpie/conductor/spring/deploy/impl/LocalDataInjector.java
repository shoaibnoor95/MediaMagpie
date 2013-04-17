package de.wehner.mediamagpie.conductor.spring.deploy.impl;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.search.jpa.FullTextEntityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.wehner.mediamagpie.conductor.persistence.dao.UserGroupDao;
import de.wehner.mediamagpie.conductor.spring.deploy.PopulateDb;
import de.wehner.mediamagpie.conductor.webapp.services.UserSecurityService;
import de.wehner.mediamagpie.core.util.ExceptionUtil;
import de.wehner.mediamagpie.persistence.PersistenceService;
import de.wehner.mediamagpie.persistence.TransactionHandler;
import de.wehner.mediamagpie.persistence.UserDao;
import de.wehner.mediamagpie.persistence.entity.ImageResizeJobExecution;
import de.wehner.mediamagpie.persistence.entity.Media;
import de.wehner.mediamagpie.persistence.entity.MediaTag;
import de.wehner.mediamagpie.persistence.entity.User;
import de.wehner.mediamagpie.persistence.entity.UserGroup;
import de.wehner.mediamagpie.persistence.entity.User.Role;

@PopulateDb
@Component
public class LocalDataInjector extends AbstractDataInjector {

    private static final Logger LOG = LoggerFactory.getLogger(LocalDataInjector.class);

    private final TransactionHandler _transactionHandler;
    private final UserGroupDao _groupDao;
    private final UserDao _userDao;
    private final PersistenceService _persistenceService;

    @Autowired
    public LocalDataInjector(TransactionHandler transactionHandler, UserGroupDao groupDao, UserDao userDao, PersistenceService persistenceService) {
        super(transactionHandler);
        _transactionHandler = transactionHandler;
        _groupDao = groupDao;
        _userDao = userDao;
        _persistenceService = persistenceService;
    }

    @Override
    public void injectData() {

        LOG.info("Start indexing lucene entities...");
        _persistenceService.beginTransaction();
        FullTextEntityManager fullTextEntityManager = _persistenceService.getFullTextEntityManager();
        try {
            fullTextEntityManager.createIndexer(Media.class).startAndWait();
            fullTextEntityManager.createIndexer(MediaTag.class).startAndWait();
        } catch (InterruptedException e) {
            throw ExceptionUtil.convertToRuntimeException(e);
        } finally {
            _persistenceService.commitTransaction();
        }
        LOG.info("Start indexing lucene entities...DONE");

        super.injectData();
        _transactionHandler.executeInTransaction(new Runnable() {

            @Override
            public void run() {
                injectInTransaction();
            }
        });
    }

    private void injectInTransaction() {
        clearPendingResizingJobs();
        List<User> users = injectUsers();
        injectGroups(users);
    }

    private void clearPendingResizingJobs() {
        List<ImageResizeJobExecution> pendingResizeJobs = _persistenceService.getAll(ImageResizeJobExecution.class);
        for (ImageResizeJobExecution imageResizeJob : pendingResizeJobs) {
            _persistenceService.remove(imageResizeJob);
        }
    }

    private List<User> injectUsers() {
        List<User> list = new ArrayList<User>();
        list.add(injectUser("admin", Role.ADMIN));
        list.add(injectUser("guest", Role.GUEST));
        list.add(injectUser("rwe", Role.USER));
        return list;
    }

    private User injectUser(String name, Role role) {
        User user = _userDao.getByName(name);
        if (user == null) {
            user = new User(name, name + "@localhost", role);
            user.setPassword(UserSecurityService.crypt(name));
            _userDao.makePersistent(user);
            LOG.warn("Injecting default user '" + name + "' with same password.");
        }
        return user;
    }

    private List<UserGroup> injectGroups(List<User> users) {
        List<UserGroup> list = new ArrayList<UserGroup>();
        list.add(injectGroup("Sales Dept.", users));
        list.add(injectGroup("IT Dept.", users));
        return list;
    }

    private UserGroup injectGroup(String name, List<User> users) {
        UserGroup group = _groupDao.getByName(name, false);
        if (group == null) {
            group = new UserGroup(name);
            for (User user : users) {
                group.addUser(user);
            }
            _groupDao.makePersistent(group);
        }
        return group;
    }

    String replaceIllegalCharacters(String string) {
        return string.replaceAll("[^a-zA-Z0-9-_]", "_");
    }
}
