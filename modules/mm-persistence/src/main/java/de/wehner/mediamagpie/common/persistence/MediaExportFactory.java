package de.wehner.mediamagpie.common.persistence;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

import de.wehner.mediamagpie.api.MediaExport;
import de.wehner.mediamagpie.api.MediaType;
import de.wehner.mediamagpie.common.persistence.entity.Media;
import de.wehner.mediamagpie.common.persistence.entity.MediaTag;

public class MediaExportFactory {

    @SuppressWarnings("unused")
    private static final Logger LOG = LoggerFactory.getLogger(MediaExportFactory.class);

    public MediaExport create(Media media) throws FileNotFoundException {
        // name
        MediaExport mediaExport = new MediaExport(media.getName());
        // file (data)
        File mediaResource = media.getFileFromUri();
        mediaExport.setInputStream(new FileInputStream(mediaResource));
        // length
        mediaExport.setLength(mediaResource.length());
        // file (original name of file)
        mediaExport.setOriginalFileName(mediaResource.getName());
        // file (hash value)
        mediaExport.setHashValue(media.getHashValue());
        // creation date
        mediaExport.setCreationDate(media.getCreationDate());
        // description
        mediaExport.setDescription(media.getDescription());
        // tags
        mediaExport.setTags(getAsList(media.getTags()));
        // id
        mediaExport.setMediaId("" + media.getId());
        // type
        mediaExport.setType(MediaType.PHOTO);

        return mediaExport;
    }

    private List<String> getAsList(List<MediaTag> tags) {
        if (tags == null || tags.size() == 0) {
            return null;
        }
        return Lists.transform(tags, new Function<MediaTag, String>() {

            @Override
            public String apply(MediaTag input) {
                return input.getName();
            }
        });
    }
}
