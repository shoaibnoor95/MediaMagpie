package de.wehner.mediamagpie.conductor.persistence.dao;

import static org.junit.Assert.*;

import static org.fest.assertions.Assertions.*;

import java.io.File;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import de.wehner.mediamagpie.persistence.dao.MediaDataProcessingJobExecutionDao;
import de.wehner.mediamagpie.persistence.dao.MediaDao;
import de.wehner.mediamagpie.persistence.dao.PersistenceService;
import de.wehner.mediamagpie.persistence.dao.ThumbImageDao;
import de.wehner.mediamagpie.persistence.entity.ImageResizeJobExecution;
import de.wehner.mediamagpie.persistence.entity.JobExecution;
import de.wehner.mediamagpie.persistence.entity.JobStatus;
import de.wehner.mediamagpie.persistence.entity.Media;
import de.wehner.mediamagpie.persistence.entity.Priority;
import de.wehner.mediamagpie.persistence.entity.ThumbImage;
import de.wehner.mediamagpie.persistence.testsupport.DbTestEnvironment;


public class ImageResizeJobExecutionDaoIntegrationTest extends AbstractDaoTest<MediaDataProcessingJobExecutionDao> {

    private ThumbImageDao _thumbImageDao;
    private MediaDataProcessingJobExecutionDao _imageResizeJobExecutionDao;
    private MediaDao _mediaDao;

    @Before
    public void setUp() {
        super.setUp();
        _thumbImageDao = new ThumbImageDao(_persistenceService);
        _imageResizeJobExecutionDao = new MediaDataProcessingJobExecutionDao(_persistenceService);
        _mediaDao = new MediaDao(_persistenceService);
    }

    @Override
    protected MediaDataProcessingJobExecutionDao createDao(PersistenceService persistenceService) {
        return new MediaDataProcessingJobExecutionDao(_persistenceService);
    }

    @Test
    public void testGetByStatus() {

        Media m1 = new Media(DbTestEnvironment.getOrCreateTestUser(_persistenceService), "ralf", new File("/data/picture1.jpg").toURI(), new Date());
        ImageResizeJobExecution jobExecution1 = new ImageResizeJobExecution(m1, "100");
        ImageResizeJobExecution jobExecution2 = new ImageResizeJobExecution(m1, "200");
        jobExecution2.setPriority(Priority.HIGH);
        ImageResizeJobExecution jobExecution3 = new ImageResizeJobExecution(m1, "300");
        _mediaDao.makePersistent(m1);
        getDao().makePersistent(jobExecution1);
        getDao().makePersistent(jobExecution2);
        getDao().makePersistent(jobExecution3);
        _persistenceService.flipTransaction();

        List<JobExecution> jobsToProcess = _imageResizeJobExecutionDao.getByStatus(Arrays.asList(JobStatus.QUEUED), 0, 10);

        assertThat(jobsToProcess).hasSize(3);
        assertThat(jobsToProcess).containsExactly(jobExecution2, jobExecution1, jobExecution3);
        _persistenceService.commitTransaction();
    }

    @Test
    public void testDeleteJobAndVerifyThatThumbImageAndMediaWillStillRemain() {
        Media m1 = new Media(DbTestEnvironment.getOrCreateTestUser(_persistenceService), "ralf", new File("/data/picture1.jpg").toURI(), new Date());
        ThumbImage thumbImage = new ThumbImage(m1);
        _mediaDao.makePersistent(m1);
        _thumbImageDao.makePersistent(thumbImage);
        ImageResizeJobExecution imageResizeJobExecution = new ImageResizeJobExecution(m1, "200");
        _imageResizeJobExecutionDao.makePersistent(imageResizeJobExecution);
        _persistenceService.flipTransaction();

        _imageResizeJobExecutionDao.makeTransient(_persistenceService.reload(imageResizeJobExecution));
        _persistenceService.flipTransaction();

        assertEquals(0, _imageResizeJobExecutionDao.getAll().size());
        assertEquals(1, _thumbImageDao.getAll().size());
        assertEquals(1, _mediaDao.getAll().size());
        _persistenceService.commitTransaction();
    }
}
