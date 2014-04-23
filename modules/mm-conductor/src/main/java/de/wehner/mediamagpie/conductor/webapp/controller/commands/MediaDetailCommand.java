package de.wehner.mediamagpie.conductor.webapp.controller.commands;

import java.io.File;
import java.io.IOException;
import java.net.URI;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.util.CollectionUtils;

import de.wehner.mediamagpie.conductor.metadata.CameraMetaData;
import de.wehner.mediamagpie.persistence.entity.Album;
import de.wehner.mediamagpie.persistence.entity.Media;

public class MediaDetailCommand extends Media {

    private String _imageLink;
    private String _overviewUrl;
    private String _tagsAsString;
    private String _urlNext;
    private String _urlPrev;
    private final Album _album;
    private CameraMetaData _cameraMetaDataObj;
    private final ObjectMapper _mapper;

    public MediaDetailCommand() {
        this(null);
    }

    public MediaDetailCommand(Album album) {
        // FIXME rwe: this would crash!!!
        this(album, null);
    }

    public MediaDetailCommand(Album album, Media media) {
        super(media);
        _album = album;
        _mapper = new ObjectMapper();
        if (!CollectionUtils.isEmpty(getTags())) {
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < getTags().size(); i++) {
                if (i > 0) {
                    builder.append(", ");
                }
                builder.append(getTags().get(i).getName());
            }
            _tagsAsString = builder.toString();
        }
        if (StringUtils.isEmpty(media.getName())) {
            try {
                setName(new File(new URI(media.getUri()).toURL().getFile()).getName());
            } catch (Exception e) {
            }
        }
        final String jsonCameraMetaData = media.getCameraMetaData();
        if (!StringUtils.isEmpty(jsonCameraMetaData) && !"null".equals(jsonCameraMetaData)) {
            try {
                _cameraMetaDataObj = _mapper.readValue(jsonCameraMetaData, CameraMetaData.class);
            } catch (JsonParseException e) {
            } catch (JsonMappingException e) {
            } catch (IOException e) {
            }
        }
    }

    public void setImageLink(String imageLink) {
        _imageLink = imageLink;
    }

    public String getImageLink() {
        return _imageLink;
    }

    public String getOverviewUrl() {
        return _overviewUrl;
    }

    public void setOverviewUrl(String overviewUrl) {
        this._overviewUrl = overviewUrl;
    }

    public String getTagsAsString() {
        return _tagsAsString;
    }

    public void setTagsAsString(String tagsAsString) {
        _tagsAsString = tagsAsString;
    }

    public String getUrlPrev() {
        return _urlPrev;
    }

    public void setUrlPrev(String urlPrev) {
        _urlPrev = urlPrev;
    }

    public String getUrlNext() {
        return _urlNext;
    }

    public void setUrlNext(String urlNext) {
        _urlNext = urlNext;
    }

    public Album getAlbum() {
        return _album;
    }

    public CameraMetaData getCameraMetaDataObj() {
        return _cameraMetaDataObj;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

}
