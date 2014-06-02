package de.wehner.mediamagpie.persistence.dao;

import static org.fest.assertions.Assertions.*;

import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import de.wehner.mediamagpie.persistence.entity.JobStatus;
import de.wehner.mediamagpie.persistence.entity.Media;
import de.wehner.mediamagpie.persistence.entity.MediaDeleteJobExecution;
import de.wehner.mediamagpie.persistence.testsupport.DbTestEnvironment;

public class MediaDeleteJobExecutionDaoTest {

    @Rule
    public DbTestEnvironment _dbTestEnvironment = new DbTestEnvironment();

    private MediaDeleteJobExecutionDao _mediaDeleteJobExecutionDao;

    private Media _m1;

    @Before
    public void setUp() throws IOException {
        _dbTestEnvironment.cleanDb();
        _dbTestEnvironment.beginTransaction();
        _mediaDeleteJobExecutionDao = _dbTestEnvironment.createDao(MediaDeleteJobExecutionDao.class);
        _m1 = _dbTestEnvironment.createNewMedia("video1");
    }

    @After
    public void tearDown() {
        _dbTestEnvironment.commitTransaction();
    }

    @Test
    public void test_hasJobForDelete_whichIsAlreadyCompleted() {

        MediaDeleteJobExecution jobExecution = new MediaDeleteJobExecution(_m1, JobStatus.COMPLETED);
        _mediaDeleteJobExecutionDao.makePersistent(jobExecution);
        _dbTestEnvironment.flipTransaction();

        boolean hasJob = _mediaDeleteJobExecutionDao.hasJobForDelete(_m1);

        assertThat(hasJob).isFalse();
    }

    @Test
    public void test_hasJobForDelete_whichIsRequeued() {

        MediaDeleteJobExecution jobExecution = new MediaDeleteJobExecution(_m1, JobStatus.RETRY);
        _mediaDeleteJobExecutionDao.makePersistent(jobExecution);
        _dbTestEnvironment.flipTransaction();

        boolean hasJob = _mediaDeleteJobExecutionDao.hasJobForDelete(_m1);

        assertThat(hasJob).isTrue();
    }
}
