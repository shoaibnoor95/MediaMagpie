package de.wehner.mediamagpie.conductor.webapp.services;

import java.io.File;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import de.wehner.mediamagpie.conductor.webapp.AbstractSpringContextTest;
import de.wehner.mediamagpie.conductor.webapp.services.JobScheduler;
import de.wehner.mediamagpie.persistence.dao.MediaDataProcessingJobExecutionDao;
import de.wehner.mediamagpie.persistence.dao.PersistenceService;
import de.wehner.mediamagpie.persistence.entity.ImageResizeJobExecution;
import de.wehner.mediamagpie.persistence.entity.JobStatus;
import de.wehner.mediamagpie.persistence.entity.Media;
import de.wehner.mediamagpie.persistence.testsupport.DbTestEnvironment;
import de.wehner.mediamagpie.persistence.testsupport.PersistenceTestUtil;


public class ImageResizeIntegrationTest extends AbstractSpringContextTest {

    @Autowired
    private PersistenceService _persistenceService;
    @Autowired
    private MediaDataProcessingJobExecutionDao _jobDao;

    private Media _media;

    @Before
    public void setUp() {
        PersistenceTestUtil.deleteAll(_persistenceService);
        _persistenceService.beginTransaction();
        _media = new Media(DbTestEnvironment.getOrCreateTestUser(_persistenceService), "name", new File("src/test/resources/images/1600x4.jpg").toURI(),
                new Date());
        _persistenceService.persist(_media);
    }

    @Test
    public void testResizeImage() {
        JobScheduler jobScheduler = _applicationContext.getBean(JobScheduler.class);

        addJobToDb(_media, "200", JobStatus.QUEUED);
        jobScheduler.start();
    }

    private void addJobToDb(Media media, String widthOrHeight, JobStatus jobStatus) {
        _persistenceService.beginTransaction();
        ImageResizeJobExecution job = new ImageResizeJobExecution(_persistenceService.reload(media), widthOrHeight);
        _jobDao.makePersistent(job);
        _persistenceService.commitTransaction();
    }
}
