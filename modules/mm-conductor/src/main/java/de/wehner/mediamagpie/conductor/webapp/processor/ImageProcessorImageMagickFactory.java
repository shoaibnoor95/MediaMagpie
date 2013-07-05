package de.wehner.mediamagpie.conductor.webapp.processor;

import java.io.File;
import java.io.IOException;

import org.apache.commons.lang3.StringUtils;

import de.wehner.mediamagpie.core.util.ProcessWrapper;
import de.wehner.mediamagpie.core.util.ProcessWrapper.StdXXXLineListener;
import de.wehner.mediamagpie.core.util.SearchPathUtil;

//@Service
public class ImageProcessorImageMagickFactory implements ImageProcessorFactory {

    private final String findPath;

    public ImageProcessorImageMagickFactory() {
        findPath = SearchPathUtil.findPath("/opt/local/bin/convert");
    }

    @Override
    public int getPerformanceIndex() {
        return 2;
    }

    @Override
    public boolean isProcessorAvailable() {

        // locate the 'convert' executable
        if (StringUtils.isEmpty(findPath)) {
            return false;
        }

        ProcessBuilder processBuilder = new ProcessBuilder(findPath, "-version");
        // Map<String, String> environ = processBuilder.environment();
        ProcessWrapper processWrapper = new ProcessWrapper(processBuilder);
        try {
            processWrapper.start(new StdXXXLineListener() {

                @Override
                public boolean fireNewLine(String line) {
                    System.out.println(line);
                    return false;
                }
            });
            int exitValue = processWrapper.waitFor();
            return (exitValue == 0);
        } catch (IOException | InterruptedException e) {
            return false;
        }
    }

    @Override
    public AbstractImageProcessor createProcessor(File originalImage) throws IOException {
        return new ImageProcessorImageIO(originalImage);
    }

}
