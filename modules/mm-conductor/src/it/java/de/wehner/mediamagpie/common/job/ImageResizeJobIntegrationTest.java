package de.wehner.mediamagpie.common.job;

import static de.wehner.mediamagpie.common.testsupport.NummericMatchers.*;

import static org.junit.Assert.*;

import java.io.File;
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
import de.wehner.mediamagpie.conductor.webapp.processor.ImageProcessorFactory;
import de.wehner.mediamagpie.conductor.webapp.processor.ImageProcessorImageIOFactory;
import de.wehner.mediamagpie.conductor.webapp.processor.ImageProcessorJAIFactory;
import de.wehner.mediamagpie.conductor.webapp.services.ImageService;
import de.wehner.mediamagpie.conductor.webapp.services.VideoService;
import de.wehner.mediamagpie.persistence.entity.JobExecution;
import de.wehner.mediamagpie.persistence.entity.Orientation;
import de.wehner.mediamagpie.persistence.entity.properties.MainConfiguration;

public class ImageResizeJobIntegrationTest {

    private static final String TEST_IMAGE_1600X4 = "src/test/resources/images/1600x4.jpg";
    private static final String TEST_VIDEO_QUICKTIME = "src/test/resources/videos/MVI_2627.MOV";

    @Rule
    public ItEnvironment _itEnvironment = new LocalItEnvironment(CleanFolderInstruction.BEFORE_CLASS);
    protected JobExecutor _jobExecutor = _itEnvironment.getJobExecutor();

    @Test
    public void testResizeImage_ImageIO_noRotation() throws Exception {
        URI doImageResize = prepareImageResizeJob(Orientation.UNKNOWN, TEST_IMAGE_1600X4, false, new ImageProcessorImageIOFactory()).call();

        // verify existence of resized image
        assertTrue(new File(doImageResize).exists());
        assertThat(new File(doImageResize).length(), almostEquals(679, 100));
    }

    @Test(expected = RuntimeException.class)
    public void testResizeImage_ImageJAI_noRotation() throws Exception {
        // expected the converson will fail because the resized image will get a size of 0x0
        URI doImageResize = prepareImageResizeJob(Orientation.UNKNOWN, TEST_IMAGE_1600X4, false, new ImageProcessorJAIFactory()).call();
        assertNotNull(doImageResize);
    }

    @Test
    public void testCreateThumbImageFromVideo_ImageJAI() throws Exception {
        URI doImageResize = prepareImageResizeJob(Orientation.UNKNOWN, TEST_VIDEO_QUICKTIME, true, new ImageProcessorImageIOFactory()).call();

        // verify existence of resized image
        assertTrue(new File(doImageResize).exists());
    }

    protected JobCallable prepareImageResizeJob(Orientation orientation, String mediaFile, boolean createImageFromVideo,
            ImageProcessorFactory... imageProcessorFactory) {
        // _itEnvironment.getTmpFile("imageResizeTest")
        ImageResizeJob imageResizeJob = new ImageResizeJob(null, null, createImageService(imageProcessorFactory), createVideoService(),
                new File(mediaFile), 1L, "200", orientation, createImageFromVideo);
        JobExecution jobExecution = _itEnvironment.createJobExecutionMock(imageResizeJob);
        JobCallable jobCallable = _jobExecutor.prepare(
                _itEnvironment.getConfigurationDaoWithMainConfiguration().getConfiguration(MainConfiguration.class), jobExecution);
        return jobCallable;
    }

    private ImageService createImageService(ImageProcessorFactory... imageProcessorFactory) {
        return new ImageService(null, null, null, null, Arrays.asList(imageProcessorFactory));
    }

    private VideoService createVideoService() {
        return new VideoService();
    }
}
