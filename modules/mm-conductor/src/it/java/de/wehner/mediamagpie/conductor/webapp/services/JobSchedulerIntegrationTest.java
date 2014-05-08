package de.wehner.mediamagpie.conductor.webapp.services;

import static org.junit.Assert.*;

import static org.fest.assertions.Assertions.*;

import static org.mockito.Matchers.*;

import static org.mockito.Mockito.*;

import java.io.File;
import java.net.URI;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.wehner.mediamagpie.conductor.performingjob.JobCallable;
import de.wehner.mediamagpie.conductor.performingjob.JobExecutor;
import de.wehner.mediamagpie.core.testsupport.TestEnvironment;
import de.wehner.mediamagpie.persistence.dao.ConfigurationDao;
import de.wehner.mediamagpie.persistence.dao.JobExecutionDao;
import de.wehner.mediamagpie.persistence.dao.PersistenceService;
import de.wehner.mediamagpie.persistence.dao.TransactionHandler;
import de.wehner.mediamagpie.persistence.entity.ImageResizeJobExecution;
import de.wehner.mediamagpie.persistence.entity.JobExecution;
import de.wehner.mediamagpie.persistence.entity.JobStatus;
import de.wehner.mediamagpie.persistence.entity.Media;
import de.wehner.mediamagpie.persistence.entity.MediaDeleteJobExecution;
import de.wehner.mediamagpie.persistence.entity.properties.MainConfiguration;
import de.wehner.mediamagpie.persistence.testsupport.DbTestEnvironment;
import de.wehner.mediamagpie.persistence.util.CipherServiceImpl;
import de.wehner.mediamagpie.persistence.util.TimeProvider;

//@RunWith(NonParallelTest.class)
public class JobSchedulerIntegrationTest {

    private static final Logger LOG = LoggerFactory.getLogger(JobSchedulerIntegrationTest.class);

    @Rule
    public DbTestEnvironment _dbTestEnvironment = new DbTestEnvironment(/* "mysql-it" */);
    @Rule
    public TestEnvironment _testEnvironment = new TestEnvironment(getClass());

    @Mock
    private JobExecutor _jobExecutor;
    @Mock
    private CipherServiceImpl _cipherService;
    private JobExecutionDao _jobExecutionDao;
    private PersistenceService _persistenceService;
    private Media _media;
    private JobScheduler _jobScheduler;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        _dbTestEnvironment.cleanDb();
        // LOG.warn("##### working with _jobExecutor: " + _jobExecutor.getClass());
        File fakeImageFile = new File(_testEnvironment.getWorkingDir(), "fake_image.png");
        FileUtils.writeStringToFile(fakeImageFile, "fake");
        _persistenceService = _dbTestEnvironment.getPersistenceService();
        _jobExecutionDao = new JobExecutionDao(_persistenceService);
        ConfigurationDao configurationDao = new ConfigurationDao(_persistenceService, _cipherService);
        _persistenceService.beginTransaction();
        _media = new Media(DbTestEnvironment.getOrCreateTestUser(_persistenceService), "name", fakeImageFile.toURI(), new Date());
        _persistenceService.persist(_media);
        _persistenceService.commitTransaction();
        TransactionHandler transactionHandler = new TransactionHandler(_persistenceService);
        _jobScheduler = new JobScheduler(transactionHandler, _jobExecutionDao, configurationDao, _jobExecutor, new TimeProvider(),
                Executors.newSingleThreadExecutor());
    }

    @Test(timeout = 15000)
    public void testStartJob() throws Exception {
        addImageResizeJobToDb(_media, "200");
        CountDownLatch jobExecutionRunning = createExecutionRunningCountDownLatch(false);

        _jobScheduler.start();
        // System.out.println("waiting for jobExecutionRunning...");
        jobExecutionRunning.await();
        // System.out.println("waiting for jobExecutionRunning...DONE");

        JobExecution jobExecutionFromDb = getJobExecutionFromDb();
        assertEquals(JobStatus.RUNNING, jobExecutionFromDb.getJobStatus());
        assertNotNull(jobExecutionFromDb.getStartTime());

        waitUntilAllJobsCompleted();

        _jobScheduler.stop();

        // verify completed job
        jobExecutionFromDb = getJobExecutionFromDb();
        assertEquals(0, jobExecutionFromDb.getRetryCount());
        assertEquals(JobStatus.COMPLETED, jobExecutionFromDb.getJobStatus());
        assertNotNull(jobExecutionFromDb.getStopTime());
        assertTrue(!StringUtils.isEmpty(jobExecutionFromDb.getCreatedDataUri()));
    }

    @Test(timeout = 15000)
    public void testStartJob_DeleteMedia() throws Exception {
        addMediaDeleteJobToDb(_media);
        CountDownLatch jobExecutionRunning = createExecutionRunningCountDownLatch(true);

        _jobScheduler.start();
        jobExecutionRunning.await();

        JobExecution jobExecutionFromDb = getJobExecutionFromDb();
        assertEquals(JobStatus.RUNNING, jobExecutionFromDb.getJobStatus());
        assertNotNull(jobExecutionFromDb.getStartTime());

        waitUntilAllJobsCompleted();

        _jobScheduler.stop();

        // verify completed job
        jobExecutionFromDb = getJobExecutionFromDb();
        assertEquals(0, jobExecutionFromDb.getRetryCount());
        assertEquals(JobStatus.COMPLETED, jobExecutionFromDb.getJobStatus());
        assertNotNull(jobExecutionFromDb.getStopTime());
        assertNull(jobExecutionFromDb.getCreatedDataUri());
    }

    @Test(timeout = 15000)
    public void testResetJobsToQueuedAfterRestart() throws Exception {
        addImageResizeJobToDb(_media, "200");

        CountDownLatch jobExecutionRunning = createExecutionRunningCountDownLatch(false);

        // the running job would be started again
        _jobScheduler.start();
        jobExecutionRunning.await();

        _jobScheduler.stop();
    }

    @Test
    public void testJobTerminatesWithError() throws Exception {
        addImageResizeJobToDb(_media, "200");

        final CountDownLatch jobExecuting = new CountDownLatch(1);

        JobCallable jobCallable = mock(JobCallable.class);
        doReturn(jobCallable).when(_jobExecutor).prepare(any(MainConfiguration.class), any(JobExecution.class));
        doAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock arg0) throws Throwable {
                jobExecuting.countDown();
                throw new IllegalArgumentException("test exception");
            }
        }).when(jobCallable).call();

        _jobScheduler.start();
        jobExecuting.await();

        waitUntilAllJobsCompleted();

        _jobScheduler.stop();

        // complete job, expected the maximum count of retries, which is 3
        JobExecution jobExecutionFromDb = getJobExecutionFromDb();
        assertEquals(3, jobExecutionFromDb.getRetryCount());
        assertEquals(JobStatus.TERMINATED_WITH_ERROR, jobExecutionFromDb.getJobStatus());
        assertNotNull(jobExecutionFromDb.getStopTime());
    }

    private CountDownLatch createExecutionRunningCountDownLatch(final boolean deleteMedia) throws Exception {
        final CountDownLatch jobExecutionRunning = new CountDownLatch(1);

        JobCallable jobCallable = mock(JobCallable.class);
        doReturn(jobCallable).when(_jobExecutor).prepare(any(MainConfiguration.class), eq(getJobExecutionFromDb())/* any(JobExecution.class) */);
        doAnswer(new Answer<URI>() {
            @Override
            public URI answer(InvocationOnMock arg0) throws Throwable {
                // LOG.warn("###############Performing JobCallable.call() an mock. That's ok.##############");
                Thread.sleep(100);
                jobExecutionRunning.countDown();
                Thread.sleep(100);
                if (deleteMedia) {
                    return null;
                }
                return new URI(_media.getUri());
            }
        }).when(jobCallable).call();
        return jobExecutionRunning;
    }

    private JobExecution getJobExecutionFromDb() {
        _persistenceService.beginTransaction();
        List<JobExecution> allFromDb = _jobExecutionDao.getAll();
        assertEquals(1, allFromDb.size());
        JobExecution dapJobExecution = allFromDb.get(0);

        _persistenceService.commitTransaction();
        return dapJobExecution;
    }

    private void addImageResizeJobToDb(Media media, String label) {
        _persistenceService.beginTransaction();
        ImageResizeJobExecution job = new ImageResizeJobExecution(_persistenceService.reload(media), label);
        _jobExecutionDao.makePersistent(job);
        _persistenceService.flipTransaction();
        assertThat(_jobExecutionDao.getAll()).hasSize(1);
        assertThat(_jobExecutionDao.getAll().get(0)).isEqualTo(job);
        _persistenceService.commitTransaction();
    }

    private void addMediaDeleteJobToDb(Media media) {
        _persistenceService.beginTransaction();
        MediaDeleteJobExecution job = new MediaDeleteJobExecution(_persistenceService.reload(media));
        _jobExecutionDao.makePersistent(job);
        _persistenceService.commitTransaction();
    }

    private void waitUntilAllJobsCompleted() throws InterruptedException {
        System.out.println("waitUntilAllJobsCompleted()...");
        while (!_jobScheduler.getRunningJobs().isEmpty()) {
            Thread.sleep(200);
        }
        // we wait for a little longer so that db transactions can be commited to the db.
        Thread.sleep(100);
        System.out.println("waitUntilAllJobsCompleted()...DONE");
    }
}
