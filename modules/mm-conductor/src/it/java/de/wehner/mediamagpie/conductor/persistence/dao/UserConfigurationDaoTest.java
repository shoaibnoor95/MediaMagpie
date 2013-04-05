package de.wehner.mediamagpie.conductor.persistence.dao;

import static org.fest.assertions.Assertions.*;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import de.wehner.mediamagpie.common.persistence.dao.UserConfigurationDao;
import de.wehner.mediamagpie.common.persistence.dao.UserDao;
import de.wehner.mediamagpie.common.persistence.entity.User;
import de.wehner.mediamagpie.common.persistence.entity.User.Role;
import de.wehner.mediamagpie.common.persistence.entity.properties.Property;
import de.wehner.mediamagpie.common.persistence.entity.properties.S3Configuration;
import de.wehner.mediamagpie.common.persistence.entity.properties.UserConfiguration;
import de.wehner.mediamagpie.common.persistence.entity.properties.UserPropertyBackedConfiguration;
import de.wehner.mediamagpie.common.persistence.testsupport.DbTestEnvironment;
import de.wehner.mediamagpie.common.util.CipherServiceImpl;
import de.wehner.mediamagpie.core.util.properties.PropertiesBacked;

public class UserConfigurationDaoTest {

    @PropertiesBacked(prefix = "user.configuration2")
    static public class OtherUserConfiguration implements UserPropertyBackedConfiguration {

        private String _pathToMedias;

        public String getPathToMedias() {
            return _pathToMedias;
        }

        public void setPathToMedias(String pathToMedias) {
            _pathToMedias = pathToMedias;
        }

        @Override
        public String toString() {
            return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((_pathToMedias == null) ? 0 : _pathToMedias.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            OtherUserConfiguration other = (OtherUserConfiguration) obj;
            if (_pathToMedias == null) {
                if (other._pathToMedias != null)
                    return false;
            } else if (!_pathToMedias.equals(other._pathToMedias))
                return false;
            return true;
        }

    };

    private CipherServiceImpl _cipherService;

    @Rule
    public DbTestEnvironment _dbTestEnvironment = new DbTestEnvironment(/* "mysql-it" */);

    private User _user;
    private UserConfigurationDao _userConfigurationDao;

    @Before
    public void setUp() {
        _dbTestEnvironment.cleanDb();
        _dbTestEnvironment.beginTransaction();
        _user = _dbTestEnvironment.getOrCreateTestUser();
        _cipherService = new CipherServiceImpl("cipherKey");
        _userConfigurationDao = new UserConfigurationDao(_dbTestEnvironment.getPersistenceService(), _cipherService);
    }

    @Test
    public void testSaveLoad() throws Exception {
        UserConfiguration configuration = new UserConfiguration();
        configuration.simpleSetSingleRootMediaPath("myPath");

        _userConfigurationDao.saveOrUpdateConfiguration(_user, configuration);
        _dbTestEnvironment.flipTransaction();
        assertThat(_dbTestEnvironment.getPersistenceService().getAll(Property.class)).hasSize(1);

        UserConfiguration configurationFromDb = _userConfigurationDao.getConfiguration(_user, UserConfiguration.class);

        try {
            assertThat(configurationFromDb).isEqualTo(configuration);
        } finally {
            _dbTestEnvironment.commitTransaction();
        }
    }

    @Test
    public void testSaveLoad_Encrypted() throws Exception {
        S3Configuration configuration = new S3Configuration();
        configuration.setSecretKey("secret key");

        _userConfigurationDao.saveOrUpdateConfiguration(_user, configuration);
        _dbTestEnvironment.flipTransaction();
        assertThat(_dbTestEnvironment.getPersistenceService().getAll(Property.class)).hasSize(2);

        S3Configuration configurationFromDb = _userConfigurationDao.getConfiguration(_user, S3Configuration.class);
        try {
            assertThat(configurationFromDb).isEqualTo(configuration);
        } finally {
            _dbTestEnvironment.commitTransaction();
        }
    }

    @Test
    public void testSaveDuplicateConfiguration() throws Exception {
        UserConfiguration configuration1 = new UserConfiguration();
        configuration1.simpleSetSingleRootMediaPath("myPath");
        UserConfiguration configuration2 = new UserConfiguration();
        configuration2.simpleSetSingleRootMediaPath("myPath2");

        _userConfigurationDao.saveOrUpdateConfiguration(_user, configuration1);
        _dbTestEnvironment.flipTransaction();
        _userConfigurationDao.saveOrUpdateConfiguration(_user, configuration2);
        _dbTestEnvironment.flipTransaction();

        UserConfiguration configurationFromDb = _userConfigurationDao.getConfiguration(_user, UserConfiguration.class);

        assertThat(configurationFromDb).isEqualTo(configuration2);
        _dbTestEnvironment.commitTransaction();
    }

    @Test
    public void testOverwriteExitingConfigurationAndLoad() throws Exception {
        UserConfiguration configuration = new UserConfiguration();
        configuration.simpleSetSingleRootMediaPath("myPath");
        _userConfigurationDao.saveOrUpdateConfiguration(_user, configuration);
        _dbTestEnvironment.flipTransaction();

        configuration.simpleSetSingleRootMediaPath("myOtherPath");
        _userConfigurationDao.saveOrUpdateConfiguration(_user, configuration);
        _dbTestEnvironment.flipTransaction();

        assertThat(_dbTestEnvironment.getPersistenceService().getAll(Property.class)).hasSize(1);
        UserConfiguration configurationFromDb = _userConfigurationDao.getConfiguration(_user, UserConfiguration.class);
        assertThat(configurationFromDb).isEqualTo(configuration);
        _dbTestEnvironment.commitTransaction();
    }

    @Test
    public void testStoreTwoDifferentConfigurationsOnSameUserAndLoad() throws Exception {
        UserConfiguration configuration = new UserConfiguration();
        configuration.simpleSetSingleRootMediaPath("myPath1");
        OtherUserConfiguration otherConfiguration = new OtherUserConfiguration();
        otherConfiguration.setPathToMedias("myPath2");
        _userConfigurationDao.saveOrUpdateConfiguration(_user, configuration);
        _userConfigurationDao.saveOrUpdateConfiguration(_user, otherConfiguration);
        _dbTestEnvironment.flipTransaction();

        assertThat(_dbTestEnvironment.getPersistenceService().getAll(Property.class)).hasSize(2);
        UserConfiguration configurationFromDb = _userConfigurationDao.getConfiguration(_user, UserConfiguration.class);
        assertThat(configurationFromDb).isEqualTo(configuration);
        OtherUserConfiguration otherConfigurationFromDb = _userConfigurationDao.getConfiguration(_user, OtherUserConfiguration.class);
        assertThat(otherConfigurationFromDb).isEqualTo(otherConfiguration);
        _dbTestEnvironment.commitTransaction();
    }

    @Test
    public void testStoreTwoUserConfigurationsForTwoUsersAndLoad() throws Exception {
        // create second user
        UserDao userDao = new UserDao(_dbTestEnvironment.getPersistenceService());
        User user2 = new User("unittest2", "test2@localhost", Role.ADMIN);
        userDao.makePersistent(user2);
        _dbTestEnvironment.flipTransaction();
        user2 = _dbTestEnvironment.reload(user2);
        // create configurations for each user
        UserConfiguration configuration1 = new UserConfiguration();
        configuration1.simpleSetSingleRootMediaPath("myPath1");
        UserConfiguration configuration2 = new UserConfiguration();
        configuration2.simpleSetSingleRootMediaPath("myPath2");
        _userConfigurationDao.saveOrUpdateConfiguration(_user, configuration1);
        _userConfigurationDao.saveOrUpdateConfiguration(user2, configuration2);
        _dbTestEnvironment.flipTransaction();

        assertThat(_dbTestEnvironment.getPersistenceService().getAll(Property.class)).hasSize(2);
        UserConfiguration configurationFromDb1 = _userConfigurationDao.getConfiguration(_user, UserConfiguration.class);
        assertThat(configurationFromDb1).isEqualTo(configuration1);
        UserConfiguration configurationFromDb2 = _userConfigurationDao.getConfiguration(user2, UserConfiguration.class);
        assertThat(configurationFromDb2).isEqualTo(configuration2);
        _dbTestEnvironment.commitTransaction();
    }

    @Test
    public void testReadFromDb() {
        UserConfiguration configuration = _userConfigurationDao.getConfiguration(_user, UserConfiguration.class);
        assertThat(configuration).isNotNull();
    }
}
