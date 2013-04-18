package de.wehner.mediamagpie.persistence.testsupport;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import javax.persistence.EntityManagerFactory;

import org.apache.commons.io.FileUtils;
import org.junit.rules.ExternalResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.wehner.mediamagpie.core.util.ExceptionUtil;
import de.wehner.mediamagpie.persistence.dao.ConfigurationDao;
import de.wehner.mediamagpie.persistence.dao.PersistenceService;
import de.wehner.mediamagpie.persistence.dao.TransactionHandler;
import de.wehner.mediamagpie.persistence.dao.UserConfigurationDao;
import de.wehner.mediamagpie.persistence.dao.UserDao;
import de.wehner.mediamagpie.persistence.entity.Base;
import de.wehner.mediamagpie.persistence.entity.Media;
import de.wehner.mediamagpie.persistence.entity.User;
import de.wehner.mediamagpie.persistence.entity.User.Role;
import de.wehner.mediamagpie.persistence.entity.properties.MainConfiguration;
import de.wehner.mediamagpie.persistence.service.ConfigurationProvider;
import de.wehner.mediamagpie.persistence.util.CipherServiceImpl;

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

    public TransactionHandler createTransactionHandler() {
        return new TransactionHandler(_persistenceService);
    }

    public ConfigurationProvider createConfigurationProvider(File workingDir) throws IOException {
        CipherServiceImpl cipherService = new CipherServiceImpl("foo");
        ConfigurationDao configurationDao = new ConfigurationDao(_persistenceService, cipherService);
        UserConfigurationDao userConfigurationDao = new UserConfigurationDao(_persistenceService, cipherService);
        ConfigurationProvider configurationProvider = new ConfigurationProvider(configurationDao, userConfigurationDao);

        MainConfiguration mainConfiguration = new MainConfiguration();
        File path = new File(workingDir, "baseUploadPath");
        FileUtils.forceMkdir(path);
        mainConfiguration.setBaseUploadPath(path.getPath());

        path = new File(workingDir, "tempMediaPath");
        FileUtils.forceMkdir(path);
        mainConfiguration.setTempMediaPath(path.getPath());

        mainConfiguration.setDefaultThumbSize(50);
        mainConfiguration.setDefaultDetailThumbSize(500);
        mainConfiguration.setDefaultGalleryDetailThumbSize(150);
        configurationProvider.saveOrUpdateMainConfiguration(mainConfiguration);

        return configurationProvider;
    }
}
