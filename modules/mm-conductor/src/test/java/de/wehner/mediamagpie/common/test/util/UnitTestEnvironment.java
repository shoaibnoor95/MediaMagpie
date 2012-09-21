package de.wehner.mediamagpie.common.test.util;

import static org.mockito.Matchers.*;

import static org.mockito.Mockito.*;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.rules.ExternalResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.wehner.mediamagpie.common.persistence.entity.JobExecution;
import de.wehner.mediamagpie.common.persistence.entity.properties.MainConfiguration;
import de.wehner.mediamagpie.conductor.performingjob.JobExecutor;
import de.wehner.mediamagpie.conductor.performingjob.JobFactory;
import de.wehner.mediamagpie.conductor.performingjob.PerformingJob;
import de.wehner.mediamagpie.conductor.persistence.TransactionHandlerMock;
import de.wehner.mediamagpie.conductor.persistence.dao.ConfigurationDao;


public class UnitTestEnvironment extends ExternalResource {

    private static final Logger LOG = LoggerFactory.getLogger(UnitTestEnvironment.class);
    
    private final File _rootDirectory;

    private final JobFactory _jobFactory;

    private final ConfigurationDao _configuraitonDao = mock(ConfigurationDao.class);
    
    public UnitTestEnvironment() {
        _rootDirectory = new File("target", "UnitTestEnvironment");
        _rootDirectory.mkdirs();
        _jobFactory = mock(JobFactory.class);
    }

    @Override
    protected void before() {
        try {
            FileUtils.deleteDirectory(_rootDirectory);
        } catch (IOException e) {
            LOG.warn("Could not delete " + _rootDirectory.getAbsolutePath());
        }
    }

    public ConfigurationDao getConfigurationDaoWithMainConfiguration() {
        MainConfiguration mainConfiguration = new MainConfiguration();
        mainConfiguration.setTempMediaPath(new File(_rootDirectory, "tempMediaPath").getPath());
        when(_configuraitonDao.getConfiguration(MainConfiguration.class)).thenReturn(mainConfiguration);
        return _configuraitonDao;
    }

    public JobExecutor createJobExecutor() {
        return new JobExecutor(_jobFactory, new TransactionHandlerMock());
    }

    public JobExecution createJobExecution(PerformingJob performingJob) {
        JobExecution jobExecution = mock(JobExecution.class);
        when(_jobFactory.createPerformingJob(/*eq(dataSource),*/ any(JobExecution.class))).thenReturn(performingJob);
        return jobExecution;
    }
}
