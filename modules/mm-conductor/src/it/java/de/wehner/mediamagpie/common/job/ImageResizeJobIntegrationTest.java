package de.wehner.mediamagpie.common.job;

import static de.wehner.mediamagpie.common.testsupport.NummericMatchers.*;

import static org.junit.Assert.*;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.util.Arrays;

import org.junit.Rule;
import org.junit.Test;

import de.wehner.mediamagpie.common.testsupport.ItEnvironment;
import de.wehner.mediamagpie.common.testsupport.ItEnvironment.CleanFolderInstruction;
import de.wehner.mediamagpie.common.testsupport.LocalItEnvironment;
import de.wehner.mediamagpie.conductor.performingjob.ImageResizeJob;
import de.wehner.mediamagpie.conductor.performingjob.JobCallable;
import de.wehner.mediamagpie.conductor.performingjob.JobExecutor;
import de.wehner.mediamagpie.conductor.webapp.media.process.ImageProcessorFactory;
import de.wehner.mediamagpie.conductor.webapp.media.process.ImageProcessorImageIOFactory;
import de.wehner.mediamagpie.conductor.webapp.media.process.ImageProcessorJAIFactory;
import de.wehner.mediamagpie.conductor.webapp.services.ImageService;
import de.wehner.mediamagpie.persistence.entity.JobExecution;
import de.wehner.mediamagpie.persistence.entity.Orientation;
import de.wehner.mediamagpie.persistence.entity.properties.MainConfiguration;

public class ImageResizeJobIntegrationTest {

    @Rule
    public ItEnvironment _itEnvironment = new LocalItEnvironment(CleanFolderInstruction.BEFORE_CLASS);
    protected JobExecutor _jobExecutor = _itEnvironment.getJobExecutor();

    @Test
    public void testResizeImage_ImageIO_noRotation() throws Exception {
        URI doImageResize = prepareImageResizeJob(Orientation.UNKNOWN, new ImageProcessorImageIOFactory()).call();

        // verify existence of resized image
        assertTrue(new File(doImageResize).exists());
        assertThat(new File(doImageResize).length(), almostEquals(679, 100));
    }

    @Test(expected = RuntimeException.class)
    public void testResizeImage_ImageJAI_noRotation() throws Exception {
        // expected the converson will fail because the resized image will get a size of 0x0
        URI doImageResize = prepareImageResizeJob(Orientation.UNKNOWN, new ImageProcessorJAIFactory()).call();
        assertNotNull(doImageResize);
    }

    protected JobCallable prepareImageResizeJob(Orientation orientation, ImageProcessorFactory... imageProcessorFactory) {
        // _itEnvironment.getTmpFile("imageResizeTest")
        ImageResizeJob imageResizeJob = new ImageResizeJob(null, null, createImageService(imageProcessorFactory), new File(
                "src/test/resources/images/1600x4.jpg"), 1L, "200", orientation);
        JobExecution jobExecution = _itEnvironment.createJobExecutionMock(imageResizeJob);
        JobCallable jobCallable = _jobExecutor.prepare(
                _itEnvironment.getConfigurationDaoWithMainConfiguration().getConfiguration(MainConfiguration.class), jobExecution);
        return jobCallable;
    }

    private ImageService createImageService(ImageProcessorFactory... imageProcessorFactory) {
        return new ImageService(null, null, null, null, Arrays.asList(imageProcessorFactory));
    }

}
