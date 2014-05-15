package de.wehner.mediamagpie.persistence.realms;

import java.io.File;

import javax.persistence.PreRemove;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.wehner.mediamagpie.persistence.entity.ThumbImage;

public class ThumbImageRelation {

    private static final Logger LOG = LoggerFactory.getLogger(ThumbImageRelation.class);

    @PreRemove
    public void removeThumbImage(Object object) {
        ThumbImage thumbImage = (ThumbImage) object;
        String pathToImage = thumbImage.getPathToImage();
        File thumbImageFile = new File(pathToImage);
        if (thumbImageFile.exists()) {
            LOG.debug("Delete thumb image file '{}' from file system.", thumbImageFile.getPath());
            thumbImageFile.delete();
        }
    }

}
