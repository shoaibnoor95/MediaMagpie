package de.wehner.mediamagpie.conductor.webapp.processor;

import java.io.File;
import java.io.IOException;

import org.springframework.stereotype.Service;

@Service
public class ImageProcessorJAIFactory implements ImageProcessorFactory {

    @Override
    public int getPerformanceIndex() {
        return 3;
    }

    @Override
    public boolean isProcessorAvailable() {
        return true;
    }

    @Override
    public AbstractImageProcessor createProcessor(File originalImage) throws IOException {
        return new ImageProcessorJAI(originalImage);
    }

}
