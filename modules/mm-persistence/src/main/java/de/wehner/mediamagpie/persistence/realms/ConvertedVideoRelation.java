package de.wehner.mediamagpie.persistence.realms;

import java.io.File;

import javax.persistence.PreRemove;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.wehner.mediamagpie.persistence.entity.ConvertedVideo;

public class ConvertedVideoRelation {

    private static final Logger LOG = LoggerFactory.getLogger(ConvertedVideoRelation.class);

    @PreRemove
    public void removeThumbImage(Object object) {
        ConvertedVideo convertedVideo = (ConvertedVideo) object;
        String pathToFile = convertedVideo.getPathToFile();
        File videoFile = new File(pathToFile);
        if (videoFile.exists()) {
            LOG.debug("Delete file '{}' from file system.", videoFile.getPath());
            videoFile.delete();
        }
    }

}
