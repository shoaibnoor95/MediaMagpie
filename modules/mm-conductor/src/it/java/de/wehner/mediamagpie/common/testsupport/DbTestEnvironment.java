package de.wehner.mediamagpie.common.testsupport;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.junit.rules.ExternalResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.wehner.mediamagpie.common.persistence.entity.Base;
import de.wehner.mediamagpie.common.persistence.entity.Media;
import de.wehner.mediamagpie.common.persistence.entity.User;
import de.wehner.mediamagpie.common.persistence.entity.User.Role;
import de.wehner.mediamagpie.common.util.ExceptionUtil;
import de.wehner.mediamagpie.conductor.persistence.PersistenceService;
import de.wehner.mediamagpie.conductor.persistence.dao.UserDao;

public class DbTestEnvironment extends ExternalResource {

    private static final Logger LOG = LoggerFactory.getLogger(DbTestEnvironment.class);

    private PersistenceService _persistenceService;

    public DbTestEnvironment() {
    }

    /**
     * @param persistenceUnit
     *            The persistence unit used to setup the db-connection. (<code>mysql-it, hsql-memory</code> etc.)
     */
    public DbTestEnvironment(String persistenceUnit) {
        // TODO rwe: no more used any more
        // System.setProperty("db.mode", persistenceUnit);
    }

    @Override
    protected void before() throws Throwable {
        _persistenceService = PersistenceTestUtil.createPersistenceService();
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
