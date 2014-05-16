package de.wehner.mediamagpie.persistence.dao;

import static org.fest.assertions.Assertions.*;

import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import de.wehner.mediamagpie.persistence.entity.Media;
import de.wehner.mediamagpie.persistence.entity.VideoConversionJobExecution;
import de.wehner.mediamagpie.persistence.testsupport.DbTestEnvironment;

public class MediaDataProcessingJobExecutionDaoTest {

    @Rule
    public DbTestEnvironment _dbTestEnvironment = new DbTestEnvironment();

    private MediaDataProcessingJobExecutionDao _jobExecutionDao;

    private Media _m1;

    @Before
    public void setUp() throws IOException {
        _dbTestEnvironment.cleanDb();
        _dbTestEnvironment.beginTransaction();
        _jobExecutionDao = new MediaDataProcessingJobExecutionDao(_dbTestEnvironment.getPersistenceService());
        _m1 = _dbTestEnvironment.createNewMedia("video1");
    }

    @After
    public void tearDown() {
        _dbTestEnvironment.commitTransaction();
    }

    @Test
    public void test_hasVideoConversionJob_WithOrHeigh_isNull() {

        VideoConversionJobExecution videoConversionJobExecution = new VideoConversionJobExecution(_m1, "WebM_vp8", null);
        _jobExecutionDao.makePersistent(videoConversionJobExecution);
        _dbTestEnvironment.flipTransaction();

        boolean hasJob = _jobExecutionDao.hasVideoConversionJob(_m1.getId(), "WebM_vp8", null);
        assertThat(hasJob).isTrue();
    }

    @Test
    public void test_hasVideoConversionJob_WithOrHeigh_isNotNull() {

        VideoConversionJobExecution videoConversionJobExecution = new VideoConversionJobExecution(_m1, "WebM_vp8", 400);
        _jobExecutionDao.makePersistent(videoConversionJobExecution);
        _dbTestEnvironment.flipTransaction();

        boolean hasJob = _jobExecutionDao.hasVideoConversionJob(_m1.getId(), "WebM_vp8", 400);
        assertThat(hasJob).isTrue();
    }
}
