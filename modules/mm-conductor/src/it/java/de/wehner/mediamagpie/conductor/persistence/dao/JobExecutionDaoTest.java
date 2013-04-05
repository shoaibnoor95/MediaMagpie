package de.wehner.mediamagpie.conductor.persistence.dao;

import static org.fest.assertions.Assertions.*;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import de.wehner.mediamagpie.common.persistence.dao.JobExecutionDao;
import de.wehner.mediamagpie.common.persistence.entity.ImageResizeJobExecution;
import de.wehner.mediamagpie.common.persistence.entity.JobExecution;
import de.wehner.mediamagpie.common.persistence.entity.Media;
import de.wehner.mediamagpie.common.persistence.testsupport.DbTestEnvironment;


public class JobExecutionDaoTest {

    @Rule
    public DbTestEnvironment _dbTestEnvironment = new DbTestEnvironment(/* "mysql-it" */);

    private JobExecutionDao _userConfigurationDao;

    @Before
    public void setUp() {
        _dbTestEnvironment.cleanDb();
        _dbTestEnvironment.beginTransaction();
        _userConfigurationDao = new JobExecutionDao(_dbTestEnvironment.getPersistenceService());
    }

    @Test
    public void testLoad() throws Exception {
        Media media = _dbTestEnvironment.createNewMedia("test1");
        ImageResizeJobExecution jobExecution = new ImageResizeJobExecution(media, "label");

        _userConfigurationDao.makePersistent(jobExecution);
        _dbTestEnvironment.flipTransaction();

        JobExecution jobExecutionFromDb = _userConfigurationDao.getById(jobExecution.getId());
        assertThat(jobExecutionFromDb).isEqualTo(jobExecution);
    }

}
