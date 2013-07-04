package de.wehner.mediamagpie.conductor.webapp.media.process;

import java.io.File;
import java.io.IOException;

public interface ImageProcessorFactory {

    int getPerformanceIndex();

    boolean isProcessorAvailable();

    AbstractImageProcessor createProcessor(File originalImage) throws IOException;

}
