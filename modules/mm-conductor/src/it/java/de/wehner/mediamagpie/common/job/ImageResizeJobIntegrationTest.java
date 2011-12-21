package de.wehner.mediamagpie.common.job;

import static de.wehner.mediamagpie.common.testsupport.NummericMatchers.*;

import static org.junit.Assert.*;

import java.io.File;
import java.net.URI;

import org.junit.Rule;
import org.junit.Test;

import de.wehner.mediamagpie.common.persistence.entity.JobExecution;
import de.wehner.mediamagpie.common.persistence.entity.Orientation;
import de.wehner.mediamagpie.common.persistence.entity.properties.MainConfiguration;
import de.wehner.mediamagpie.common.testsupport.ItEnvironment;
import de.wehner.mediamagpie.common.testsupport.ItEnvironment.CleanFolderInstruction;
import de.wehner.mediamagpie.common.testsupport.LocalItEnvironment;
import de.wehner.mediamagpie.conductor.performingjob.ImageResizeJob;
import de.wehner.mediamagpie.conductor.performingjob.JobCallable;
import de.wehner.mediamagpie.conductor.performingjob.JobExecutor;

// see ImportJobTest in dap project
public class ImageResizeJobIntegrationTest {

    @Rule
    public ItEnvironment _itEnvironment = new LocalItEnvironment(CleanFolderInstruction.BEFORE_CLASS);
    protected JobExecutor _jobExecutor = _itEnvironment.getJobExecutor();

    @Test
    public void testResizeImage() throws Exception {
        URI doImageResize = doImageResize();

        // verify existence of resized image
        assertTrue(new File(doImageResize).exists());
        assertThat(new File(doImageResize).length(), almostEquals(679, 100));
    }

    protected URI doImageResize() throws Exception {
        return prepareImageResizeJob().call();
    }

    protected JobCallable prepareImageResizeJob() {
        // _itEnvironment.getTmpFile("imageResizeTest")
        ImageResizeJob imageResizeJob = new ImageResizeJob(null, null, new File("src/test/resources/images/1600x4.jpg"), 1L, "200", Orientation.UNKNOWN);
        JobExecution dapJobExecution = _itEnvironment.createJobExecutionMock(imageResizeJob);
        JobCallable jobCallable = _jobExecutor.prepare(
                _itEnvironment.getConfigurationDaoWithMainConfiguration().getConfiguration(MainConfiguration.class), dapJobExecution);
        return jobCallable;
    }

}
