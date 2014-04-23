package de.wehner.mediamagpie.conductor.webapp.services;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import de.wehner.mediamagpie.conductor.media.FfmpegWrapper;

@Service
public class VideoService {

    public static final Logger LOG = LoggerFactory.getLogger(VideoService.class);

    public File createImageFromVideo(File originImage, long mediaId, File tempMediaPath) {
        File outputFile = new File(tempMediaPath, originImage.getName() + ".img1.jpg");
        FfmpegWrapper ffmpegWrapper = new FfmpegWrapper(originImage.toURI());
        return ffmpegWrapper.createImageFromVideo(outputFile);
    }
}
