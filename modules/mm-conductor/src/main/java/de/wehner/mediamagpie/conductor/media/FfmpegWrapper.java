package de.wehner.mediamagpie.conductor.media;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.wehner.mediamagpie.conductor.metadata.VideoMetaData;
import de.wehner.mediamagpie.core.util.ProcessWrapper;
import de.wehner.mediamagpie.core.util.ProcessWrapper.StdXXXLineListener;
import de.wehner.mediamagpie.core.util.SearchPathUtil;

public class FfmpegWrapper {

    private static final Logger LOG = LoggerFactory.getLogger(FfmpegWrapper.class);

    private final String ffmpegBinary;
    private final File videoFile;

    public FfmpegWrapper(URI videoSource) {
        super();
        ffmpegBinary = SearchPathUtil.findPath("/opt/local/bin/ffmpeg");
        if (videoSource.getScheme().equals("file") && new File(videoSource.getPath()).exists()) {
            this.videoFile = new File(videoSource);
        } else {
            LOG.error("Can not handle video sources which are not stored within local file system or which are not present. ({})", videoSource);
            this.videoFile = null;
        }
    }

    public VideoMetaData createVideoMetadata() {
        VideoMetaData videoMetaData = new VideoMetaData();
        ProcessBuilder pb = new ProcessBuilder(ffmpegBinary, "-i", videoFile.getPath(), "-f", "ffmetadata");
        final List<String> output = new ArrayList<>();
        ProcessWrapper processWrapper = new ProcessWrapper(pb);
        try {
            processWrapper.start(new StdXXXLineListener() {

                @Override
                public boolean fireNewLine(String line) {
                    LOG.trace(line);
                    output.add(line);
                    return true;
                }
            });
            videoMetaData.setFfmpegLines(output);
        } catch (IOException e) {
            LOG.warn("ffmpeg cound not be started. Probably it is not installed on this system or it can not be found.", e);
            return null;
        }

        processWrapper.waitUntilFinished(5, TimeUnit.MINUTES);
        return videoMetaData;
    }

    public File createImageFromVideo(File outputFile) {
        if (!outputFile.getParentFile().exists()) {
            LOG.info("create folder '{}‘.", outputFile.getParent());
            try {
                FileUtils.forceMkdir(outputFile.getParentFile());
            } catch (IOException e) {
                throw new RuntimeException("internal error", e);
            }
        }
        ProcessBuilder pb = new ProcessBuilder(ffmpegBinary, "-i", videoFile.getPath(), "-r", "1", "-t", "1", outputFile.getPath());
        ProcessWrapper processWrapper = new ProcessWrapper(pb);
        try {
            processWrapper.start(new StdXXXLineListener() {

                @Override
                public boolean fireNewLine(String line) {
                    LOG.trace(line);
                    return true;
                }
            });
        } catch (IOException e) {
            LOG.warn("ffmpeg cound not be started. Probably it is not installed on this system or it can not be found.", e);
            return null;
        }

        processWrapper.waitUntilFinished(5, TimeUnit.SECONDS);
        return outputFile;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
