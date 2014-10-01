package de.wehner.mediamagpie.conductor.webapp.processor;

import org.junit.Test;

public class ImageProcessorImageMagickFactoryTest {

    @Test
    public void test() {
        // checkout if ffmpeg is available otherwise quit test
        org.junit.Assume.assumeTrue(ImageProcessorImageMagickFactory.getConvertPath(true) != null);

        ImageProcessorImageMagickFactory factory = new ImageProcessorImageMagickFactory();

        factory.isProcessorAvailable();
    }
}
