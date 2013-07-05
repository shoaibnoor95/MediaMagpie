package de.wehner.mediamagpie.conductor.webapp.processor;

import java.io.File;
import java.io.IOException;

public interface ImageProcessorFactory {

    int getPerformanceIndex();

    boolean isProcessorAvailable();

    AbstractImageProcessor createProcessor(File originalImage) throws IOException;

}
