package de.wehner.mediamagpie.conductor.webapp.processor;

import static org.fest.assertions.Assertions.*;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.apache.commons.io.FilenameUtils;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.util.StopWatch;

import de.wehner.mediamagpie.conductor.webapp.processor.ImageProcessorImageIO;
import de.wehner.mediamagpie.conductor.webapp.services.ImageService;
import de.wehner.mediamagpie.core.testsupport.TestEnvironment;

public class ImageProcessorImageIOTest {

    private static final String SRC_IMAGE_UPRIGHT = "src/test/resources/images/IMG_1414.JPG";

    static final int RESIZE_W = 1500;
    static final int RESIZE_H = 600;

    private final int LOOP_COUNT = 20;

    @Rule
    public TestEnvironment _testEnvironment = new TestEnvironment(ImageProcessorImageIOTest.class);

    @Test
    public void test_resizeImageWithAffineTransform_NTimes() throws IOException {
        File originMediaFile = new File(SRC_IMAGE_UPRIGHT);
        File resizedImageFile = new File(_testEnvironment.getWorkingDir(), "resizedrotated_6.jpg");

        BufferedImage originBitmap = ImageIO.read(originMediaFile);
        BufferedImage destImage = null;
        for (int i = 0; i < LOOP_COUNT; i++) {
            destImage = ImageProcessorImageIO.resizeImageWithAffineTransform(originBitmap,
                    ImageService.computeNewDimension(originBitmap.getWidth(), originBitmap.getHeight(), RESIZE_W, RESIZE_H));
            destImage = ImageProcessorImageIO.rotateImage(destImage, 90.0);
            System.out.println(i);
        }
        assertThat(ImageIO.write(destImage, FilenameUtils.getExtension(resizedImageFile.getPath()), resizedImageFile)).isTrue();
    }

    @Test
    public void test_rotateImage_270degree() throws IOException {
        File originMediaFile = new File(SRC_IMAGE_UPRIGHT);
        File rotatedImageFile = new File(_testEnvironment.getWorkingDir(), "rotated.jpg");

        BufferedImage originBitmap = ImageIO.read(originMediaFile);
        BufferedImage destImage = ImageProcessorImageIO.rotateImage(originBitmap, 270.0);
        assertThat(ImageIO.write(destImage, FilenameUtils.getExtension(rotatedImageFile.getPath()), rotatedImageFile)).isTrue();
    }

    @Test
    public void test_rotateImage_NTimes() throws IOException {
        File originMediaFile = new File(SRC_IMAGE_UPRIGHT);
        File resizedImageFile = new File(_testEnvironment.getWorkingDir(), "resizedrotated_6.jpg");
        StopWatch stopWatch = new StopWatch();

        ImageProcessorImageIO processor = new ImageProcessorImageIO(originMediaFile);

        for (int i = 0; i < LOOP_COUNT; i++) {
            stopWatch.start((i + 1) + ". rotate 90 degrees");
            processor.rotateImage(90);
            stopWatch.stop();
        }
        System.out.println(stopWatch.prettyPrint());
        processor.write(resizedImageFile);
        processor.close();
        assertThat(resizedImageFile).exists();
    }
}
