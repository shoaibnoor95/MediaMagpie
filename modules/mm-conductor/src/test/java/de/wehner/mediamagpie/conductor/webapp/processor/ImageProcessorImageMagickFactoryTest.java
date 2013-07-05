package de.wehner.mediamagpie.conductor.webapp.processor;

import org.junit.Test;

public class ImageProcessorImageMagickFactoryTest {

    
    @Test
    public void test() {
        ImageProcessorImageMagickFactory factory = new ImageProcessorImageMagickFactory();
        
        factory.isProcessorAvailable();
    }
}
