package de.wehner.mediamagpie.common.testsupport;

import static org.mockito.Matchers.*;

import static org.mockito.Mockito.*;

import java.io.File;
import java.io.IOException;

import org.junit.rules.ExternalResource;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import de.wehner.mediamagpie.conductor.performingjob.JobExecutor;
import de.wehner.mediamagpie.conductor.performingjob.JobFactory;
import de.wehner.mediamagpie.conductor.performingjob.PerformingJob;
import de.wehner.mediamagpie.core.testsupport.TestEnvironment;
import de.wehner.mediamagpie.persistence.TransactionHandlerMock;
import de.wehner.mediamagpie.persistence.dao.ConfigurationDao;
import de.wehner.mediamagpie.persistence.entity.JobExecution;
import de.wehner.mediamagpie.persistence.entity.properties.MainConfiguration;

/**
 * Gives access to the 'environment' needed for executing a job.
 * 
 */
public abstract class ItEnvironment extends ExternalResource {

    public static final String SYSTEM_PROPERTY_EC2_TESTS_ENABLED = "ec2.tests.enabled";

    @Mock
    private ConfigurationDao _configurationDao;

    // @Mock
    // private UserConfiguration _userConfiguration;

    public enum CleanFolderInstruction {
        BEFORE_CLASS, BEFORE;
    }

    public enum ItMode {
        LOCAL_OR_DISTRIBUTED_HADOOP, EMR, SELENIUM;
    }

    private final CleanFolderInstruction _cleanInstruction;
    protected final File _rootTmpDirectory;
    private final JobFactory _jobFactory;
    private final JobExecutor _jobExecutor;

    @Override
    protected void before() throws Throwable {
        MockitoAnnotations.initMocks(this);
    };

    public ItEnvironment(CleanFolderInstruction cleanInstruction) {
        _cleanInstruction = cleanInstruction;
        _rootTmpDirectory = new File("target", "IntTestEnvironment");
        _rootTmpDirectory.mkdirs();
        _jobFactory = mock(JobFactory.class);
        _jobExecutor = new JobExecutor(_jobFactory, new TransactionHandlerMock());
        if (cleanInstruction == CleanFolderInstruction.BEFORE_CLASS) {
            try {
                TestEnvironment.cleanDir(_rootTmpDirectory);
            } catch (IOException e) {
            }
        }
    }

    public ConfigurationDao getConfigurationDaoWithMainConfiguration() {
        MainConfiguration mainConfiguration = new MainConfiguration();
        mainConfiguration.setTempMediaPath(getTmpFile("tempMediaPath").getPath());
        when(_configurationDao.getConfiguration(MainConfiguration.class)).thenReturn(mainConfiguration);
        return _configurationDao;
    }

    public JobExecutor getJobExecutor() {
        return _jobExecutor;
    }

    public File getTmpFile(String name) {
        return new File(_rootTmpDirectory, name);
    }

    //
    public JobExecution createJobExecutionMock(PerformingJob performingJob) {
        JobExecution jobExecution = mock(JobExecution.class);
        when(_jobFactory.createPerformingJob(any(JobExecution.class))).thenReturn(performingJob);
        when(jobExecution.getId()).thenReturn(0L);
        return jobExecution;
    }

    public static boolean isAwsEnabled() {
        return "true".equals(System.getProperty(SYSTEM_PROPERTY_EC2_TESTS_ENABLED));
    }

    public static void chechAwsEnabled() {
        if (!isAwsEnabled()) {
            throw new RuntimeException("aws tests not enabled - check -D" + SYSTEM_PROPERTY_EC2_TESTS_ENABLED);
        }
    }

}
