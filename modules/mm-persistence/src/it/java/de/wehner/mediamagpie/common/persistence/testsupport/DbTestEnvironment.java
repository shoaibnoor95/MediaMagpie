package de.wehner.mediamagpie.common.persistence.testsupport;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import javax.persistence.EntityManagerFactory;

import org.apache.commons.io.FileUtils;
import org.junit.rules.ExternalResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.wehner.mediamagpie.common.persistence.dao.UserDao;
import de.wehner.mediamagpie.common.persistence.entity.Base;
import de.wehner.mediamagpie.common.persistence.entity.Media;
import de.wehner.mediamagpie.common.persistence.entity.User;
import de.wehner.mediamagpie.common.persistence.entity.User.Role;
import de.wehner.mediamagpie.core.util.ExceptionUtil;
import de.wehner.mediamagpie.persistence.PersistenceService;

public class DbTestEnvironment extends ExternalResource {

    private static final Logger LOG = LoggerFactory.getLogger(DbTestEnvironment.class);

    private EntityManagerFactory _entityManagerFactory;

    private PersistenceService _persistenceService;

    public DbTestEnvironment() {
    }

    @Override
    protected void before() throws Throwable {
        _entityManagerFactory = PersistenceTestUtil.createEntityManagerFactory();
        _persistenceService = new PersistenceService(_entityManagerFactory);
        super.before();
    }

    @Override
    protected void after() {
        cleanDb();
        File luceneIndex = new File("target/runtime/lucene_test");
        if (luceneIndex.exists()) {
            LOG.info("delete lucene index directory '" + luceneIndex.getPath() + "'.");
            try {
                FileUtils.deleteDirectory(luceneIndex);
            } catch (IOException e) {
                ExceptionUtil.convertToRuntimeException(e);
            }
        }
        _entityManagerFactory.close();
        super.after();
    }

    public PersistenceService getPersistenceService() {
        return _persistenceService;
    }

    public void cleanDb() {
        PersistenceTestUtil.deleteAll(_persistenceService);
    }

    public void beginTransaction() {
        _persistenceService.beginTransaction();
    }

    public void flipTransaction() {
        _persistenceService.flipTransaction();
    }

    @SuppressWarnings("unchecked")
    public <T extends Base> T reload(T entity) {
        return (T) _persistenceService.getById(entity.getClass(), entity.getId());
    }

    public void commitTransaction() {
        _persistenceService.commitTransaction();
    }

    public User getOrCreateTestUser() {
        return getOrCreateTestUser(_persistenceService);
    }

    public static User getOrCreateTestUser(PersistenceService persistenceService) {
        UserDao userDao = new UserDao(persistenceService);
        User user = userDao.getByName("unittest");
        if (user == null) {
            user = new User("unittest", "test@localhost", Role.ADMIN);
            userDao.makePersistent(user);
            persistenceService.flipTransaction();
            return persistenceService.reload(user);
        }
        return user;
    }

    public Media createNewMedia(String name) {
        Media media = new Media(getOrCreateTestUser(), name, new File("/tmp/pic_" + name + ".png").toURI(), new Date());
        _persistenceService.persist(media);
        return media;
    }
}
