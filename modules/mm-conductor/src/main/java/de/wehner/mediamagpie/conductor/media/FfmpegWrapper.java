package de.wehner.mediamagpie.conductor.media;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.wehner.mediamagpie.conductor.metadata.VideoMetaData;
import de.wehner.mediamagpie.conductor.webapp.services.VideoService.VideoFormat;
import de.wehner.mediamagpie.core.util.ProcessWrapper;
import de.wehner.mediamagpie.core.util.ProcessWrapper.StdXXXLineListener;
import de.wehner.mediamagpie.core.util.SearchPathUtil;

public class FfmpegWrapper {

    private static final Logger LOG = LoggerFactory.getLogger(FfmpegWrapper.class);

    private final String ffmpegBinary;
    private final File videoFile;

    public FfmpegWrapper(URI videoSource) {
        super();
        ffmpegBinary = getFfmpegBinaryPath(false);
        if (videoSource.getScheme().equals("file") && new File(videoSource.getPath()).exists()) {
            this.videoFile = new File(videoSource);
        } else {
            LOG.error("Can not handle video sources which are not stored within local file system or which are not present. ({})", videoSource);
            this.videoFile = null;
        }
    }

    public static String getFfmpegBinaryPath(boolean test) {
        try {
            return SearchPathUtil.findPath("/opt/local/bin/ffmpeg", "/usr/bin/ffmpeg");
        } catch (Exception e) {
            if (!test) {
                throw e;
            }
            return null;
        }
    }

    public VideoMetaData createVideoMetadata() {
        VideoMetaData videoMetaData = new VideoMetaData();
        ProcessBuilder pb = new ProcessBuilder(ffmpegBinary, "-i", videoFile.getPath(), "-f", "ffmetadata");
        final List<String> output = new ArrayList<>();
        if (runExternalProcess(pb, 5, TimeUnit.SECONDS, output)) {
            videoMetaData.setFfmpegLines(output);
        }

        return videoMetaData;
    }

    public File createImageFromVideo(File outputFile) {
        createOuputFolderIfNotExist(outputFile);
        ProcessBuilder pb = new ProcessBuilder(ffmpegBinary, "-i", videoFile.getPath(), "-r", "1", "-t", "1", outputFile.getPath());
        runExternalProcess(pb, 5, TimeUnit.SECONDS, null);
        return outputFile;
    }

    public File convertVideo(File outputFile, VideoFormat destFormat, Integer width) {
        createOuputFolderIfNotExist(outputFile);
        List<String> arguments = new ArrayList<>(Arrays.asList(ffmpegBinary, "-i", videoFile.getPath()));
        switch (destFormat) {
        case WebM_vp8:
            // *.webm
            // ffmpeg -i input.mov -acodec libvorbis -ac 2 -ab 96k -ar 44100 -b 345k -s 640x360 output.webm
            // arguments.addAll(Arrays.asList("-acodec", "libvorbis", "-ac", "2"/*, "-ab", "96k", "-ar", "44100", "-b:v", "345k"*/));
            // arguments.addAll(Arrays.asList("-codec:v", "libvorbis", "-qscale:v", "7", "-codec:a", "libvorbis", "-qscale:a", "5"));
            // ffmpeg -i input -vcodec libvpx -cpu-used -5 -deadline realtime out.webm
            // rwe: fast, but less quality: arguments.addAll(Arrays.asList("-vcodec", "libvpx", "-cpu-used", "-5", "-deadline",
            // "realtime"));
            // ffmpeg -i input-file.mp4 -c:v libvpx -crf 10 -b:v 1M -c:a libvorbis output-file.webm
            arguments.addAll(Arrays.asList("-codec:v", "libvpx", "-crf", "10", "-b:v", "1M", "-c:a", "libvorbis"));
            break;
        case OGG_Theora:
            // *.ogv
            // ffmpeg -i input.mov -acodec libvorbis -ac 2 -ab 96k -ar 44100 -b 345k -s 640x360 output.ogv
            arguments.addAll(Arrays.asList("-acodec", "libvorbis", "-ac", "2"/* , "-ab", "96k" */, "-ar", "44100", "-b:v", "1M"));
            // ffmpeg -i input.mkv -codec:v libtheora -qscale:v 7 -codec:a libvorbis -qscale:a 5 output.ogv
            // rwe: good result, but big file: arguments.addAll(Arrays.asList("-codec:v", "libtheora", "-qscale:v", "7", "-codec:a",
            // "libvorbis", "-qscale:a", "5"));
            break;
        case MP4_h264:
            // ffmpeg -i input.mov -acodec libfaac -ab 96k -vcodec libx264 -vpre slower -vpre main -level 21 -refs 2 -b 345k -bt 345k
            // -threads 0 -s 640x360 output.mp4
            // see: http://superuser.com/questions/525249/convert-avi-to-mp4-keeping-the-same-quality
            // ffmpeg -i input.avi -c:v libx264 -crf 19 -preset slow -c:a libfaac -b:a 192k -ac 2 out.mp4
            arguments.addAll(Arrays.asList("-c:v", "libx264", "-crf", "19", "-preset", "slow", "-c:a", "libfaac", /* "-b:a", "192k", */"-ac", "2"));
            break;
        default:
            throw new RuntimeException("Internal error: Not supported format.");
        }
        // add option size
        if (width != null) {
            // Add (WxH or abbreviation)
            // arguments.add("-s");
            // arguments.add("640x360");
            arguments.add("-vf");
            arguments.add("scale=" + width + ":trunc(ow/a/2)*2");
        }
        // add output file name
        arguments.add(outputFile.getPath());

        ProcessBuilder pb = new ProcessBuilder(arguments);
        runExternalProcess(pb, 5, TimeUnit.MINUTES, null);
        return outputFile;
    }

    private boolean runExternalProcess(ProcessBuilder pb, int timeOut, TimeUnit timeUnit, final List<String> collectOutputLines) {
        ProcessWrapper processWrapper = new ProcessWrapper(pb);
        try {
            processWrapper.start(new StdXXXLineListener() {

                @Override
                public boolean fireNewLine(String line) {
                    LOG.trace(line);
                    if (collectOutputLines != null) {
                        collectOutputLines.add(line);
                    }
                    return true;
                }
            });
        } catch (IOException e) {
            LOG.warn("ffmpeg cound not be started. Probably it is not installed on this system or it can not be found.", e);
            return false;
        }

        return processWrapper.waitUntilFinished(timeOut, timeUnit);
    }

    private void createOuputFolderIfNotExist(File outputFile) {
        if (!outputFile.getParentFile().exists()) {
            LOG.info("create folder '{}‘.", outputFile.getParent());
            try {
                FileUtils.forceMkdir(outputFile.getParentFile());
            } catch (IOException e) {
                throw new RuntimeException("internal error", e);
            }
        }
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
