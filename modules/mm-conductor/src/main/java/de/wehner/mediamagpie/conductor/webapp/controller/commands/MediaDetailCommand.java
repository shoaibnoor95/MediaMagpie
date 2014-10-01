package de.wehner.mediamagpie.conductor.webapp.controller.commands;

import java.io.File;
import java.io.IOException;
import java.net.URI;

import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.util.CollectionUtils;

import de.wehner.mediamagpie.conductor.metadata.CameraMetaData;
import de.wehner.mediamagpie.persistence.entity.Album;
import de.wehner.mediamagpie.persistence.entity.Media;

public class MediaDetailCommand extends Media {

    private String _videoUrl;
    private String _imageLink;
    private String _overviewUrl;
    private String _tagsAsString;
    private String _urlNext;
    private String _urlPrev;
    private Album _album;
    private CameraMetaData _cameraMetaDataObj;
    private final ObjectMapper _mapper = new ObjectMapper();
    private final static MapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();

    public MediaDetailCommand() {
        super();
    }

    public static MediaDetailCommand createFromMedia(Media media) {

        MediaDetailCommand mediaDetailCommand = mapperFactory.getMapperFacade().map(media, MediaDetailCommand.class);
        if (!CollectionUtils.isEmpty(mediaDetailCommand.getTags())) {
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < mediaDetailCommand.getTags().size(); i++) {
                if (i > 0) {
                    builder.append(", ");
                }
                builder.append(mediaDetailCommand.getTags().get(i).getName());
            }
            mediaDetailCommand._tagsAsString = builder.toString();
        }
        if (StringUtils.isEmpty(media.getName())) {
            try {
                mediaDetailCommand.setName(new File(new URI(media.getUri()).toURL().getFile()).getName());
            } catch (Exception e) {
            }
        }
        final String jsonCameraMetaData = media.getCameraMetaData();
        if (!StringUtils.isEmpty(jsonCameraMetaData) && !"null".equals(jsonCameraMetaData)) {
            try {
                mediaDetailCommand._cameraMetaDataObj = mediaDetailCommand._mapper.readValue(jsonCameraMetaData, CameraMetaData.class);
            } catch (JsonParseException e) {
            } catch (JsonMappingException e) {
            } catch (IOException e) {
            }
        }

        return mediaDetailCommand;
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

    public void setAlbum(Album album) {
        _album = album;
    }

    public CameraMetaData getCameraMetaDataObj() {
        return _cameraMetaDataObj;
    }

    public boolean isPhoto() {
        return (StringUtils.isEmpty(getMediaType()) || getMediaType().startsWith(Media.IMAGE_PREFIX));
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

    public void setVideoUrl(String videoUrl) {
        _videoUrl = videoUrl;
    }

    public String getVideoUrl() {
        return _videoUrl;
    }

}
