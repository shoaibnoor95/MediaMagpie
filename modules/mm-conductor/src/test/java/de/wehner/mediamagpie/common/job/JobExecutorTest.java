package de.wehner.mediamagpie.common.job;

import static org.junit.Assert.*;

import static org.mockito.Matchers.*;

import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import de.wehner.mediamagpie.common.test.util.UnitTestEnvironment;
import de.wehner.mediamagpie.conductor.performingjob.JobCallable;
import de.wehner.mediamagpie.conductor.performingjob.JobExecutor;
import de.wehner.mediamagpie.conductor.performingjob.PerformingJob;
import de.wehner.mediamagpie.conductor.performingjob.PerformingJobContext;
import de.wehner.mediamagpie.persistence.entity.JobExecution;
import de.wehner.mediamagpie.persistence.entity.properties.MainConfiguration;

public class JobExecutorTest {

    @Rule
    public UnitTestEnvironment _unitTestEnvironment = new UnitTestEnvironment();

    private JobExecutor _jobExecutor;

    @Mock
    private PerformingJob _performingJob;

    @Mock
    private JobCallable _jobCallable;

    private JobExecution _jobExecution;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        when(_performingJob.prepare()).thenReturn(_jobCallable);
        _jobExecutor = _unitTestEnvironment.createJobExecutor();
        _jobExecution = _unitTestEnvironment.createJobExecution(_performingJob);
    }

    @Test
    public void testExecute() throws Exception {
        _jobExecutor.execute(_unitTestEnvironment.getConfigurationDaoWithMainConfiguration().getConfiguration(MainConfiguration.class), _jobExecution);

        InOrder inOrder = inOrder(_performingJob, _jobCallable);
        inOrder.verify(_performingJob).init(any(PerformingJobContext.class));
        inOrder.verify(_performingJob).prepare();
        inOrder.verify(_jobCallable).call();
    }

    @Test
    public void testLoggingErrors() throws Exception {
        doThrow(new RuntimeException("test exception")).when(_performingJob).init(any(PerformingJobContext.class));

        try {
            _jobExecutor.execute(_unitTestEnvironment.getConfigurationDaoWithMainConfiguration().getConfiguration(MainConfiguration.class),
                    _jobExecution);
            fail();
        } catch (RuntimeException e) {
            // expected
        }

    }

}
