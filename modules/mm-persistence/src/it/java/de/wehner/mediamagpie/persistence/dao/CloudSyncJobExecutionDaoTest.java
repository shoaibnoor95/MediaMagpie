package de.wehner.mediamagpie.persistence.dao;

import static org.fest.assertions.Assertions.*;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import de.wehner.mediamagpie.persistence.CloudSyncJobExecutionDao;
import de.wehner.mediamagpie.persistence.UserConfigurationDao;
import de.wehner.mediamagpie.persistence.entity.CloudSyncJobExecution;
import de.wehner.mediamagpie.persistence.entity.User;
import de.wehner.mediamagpie.persistence.entity.CloudSyncJobExecution.CloudType;
import de.wehner.mediamagpie.persistence.entity.properties.UserConfiguration;
import de.wehner.mediamagpie.persistence.testsupport.DbTestEnvironment;

public class CloudSyncJobExecutionDaoTest {

    @Rule
    public DbTestEnvironment _dbTestEnvironment = new DbTestEnvironment();

    private User _user;

    private CloudSyncJobExecutionDao _dao;

    @Before
    public void setUp() {
        _dbTestEnvironment.cleanDb();
        _dbTestEnvironment.beginTransaction();
        _user = _dbTestEnvironment.getOrCreateTestUser();
        _dao = new CloudSyncJobExecutionDao(_dbTestEnvironment.getPersistenceService());
    }

    @Test
    public void testPersistAndLoad() throws Exception {
        CloudSyncJobExecution jobExecution = new CloudSyncJobExecution(_user, CloudType.S3);

        _dao.makePersistent(jobExecution);
        _dbTestEnvironment.flipTransaction();

        CloudSyncJobExecution jobExecutionFromDb = (CloudSyncJobExecution) _dao.getById(jobExecution.getId());
        assertThat(jobExecutionFromDb.getUser().getId()).isEqualTo(_user.getId());
    }

    @Test
    public void testPersistAndLoad_AndLoadUserConfiguration() throws Exception {
        CloudSyncJobExecution jobExecution = new CloudSyncJobExecution(_user, CloudType.S3);
        UserConfigurationDao userConfigurationDao = new UserConfigurationDao(_dbTestEnvironment.getPersistenceService(), null);
        UserConfiguration userConfiguration1 = userConfigurationDao.getConfiguration(_user, UserConfiguration.class);

        _dao.makePersistent(jobExecution);
        _dbTestEnvironment.flipTransaction();

        CloudSyncJobExecution jobExecutionFromDb = (CloudSyncJobExecution) _dao.getById(jobExecution.getId());
        UserConfiguration userConfiguration2 = userConfigurationDao.getConfiguration(jobExecutionFromDb.getUser(), UserConfiguration.class);
        assertThat(userConfiguration2).isEqualTo(userConfiguration1);
    }
}
