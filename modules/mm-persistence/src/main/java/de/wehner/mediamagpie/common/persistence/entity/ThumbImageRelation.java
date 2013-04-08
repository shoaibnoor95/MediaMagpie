package de.wehner.mediamagpie.common.persistence.entity;

import java.io.File;

import javax.persistence.PreRemove;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ThumbImageRelation {

    private static final Logger LOG = LoggerFactory.getLogger(ThumbImageRelation.class);

    @PreRemove
    public void removeThumbImage(Object object) {
        ThumbImage thumbImage = (ThumbImage) object;
        String pathToImage = thumbImage.getPathToImage();
        File thumbImageFile = new File(pathToImage);
        if (thumbImageFile.exists()) {
            LOG.debug("Delete image '" + thumbImageFile.getPath() + "' from file system.");
            thumbImageFile.delete();
        }
    }

}