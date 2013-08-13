package de.wehner.mediamagpie.conductor.webapp.controller.commands;

import java.io.File;
import java.net.URI;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.commons.lang3.StringUtils;

import de.wehner.mediamagpie.conductor.metadata.CameraMetaData;
import de.wehner.mediamagpie.persistence.entity.Media;

public class MediaThumbCommand {

    /**
     * ID of media
     */
    private long _id;

    /**
     * The url to the thumb image that is shown
     */
    private String _urlThumbImage;

    /**
     * The url to a thumb image that is bigger than <code>_urlThumbImage</code> and shows more detail. This image can an original photo but
     * mostly it will just be a bigger resized thumb image.
     */
    private String _urlThumbDetail;

    /**
     * The url to the original photo or video
     */
    private String _urlDownload;

    /**
     * The title which is shown when the cursor hang over the thumb image
     */
    private String _title;

    /**
     * A description to the media
     */
    private String _description;

    /**
     * Metadata of camera like exif data
     */
    private CameraMetaData _cameraMetaData;

    /**
     * The media entity, eg. used to get meta information
     */
    private Media _media;

    private boolean _s3Available;

    public MediaThumbCommand() {
        this(null);
    }

    public MediaThumbCommand(Media media) {
        super();
        _cameraMetaData = new CameraMetaData();
        _media = media;
    }

    public long getId() {
        return _id;
    }

    public void setId(long id) {
        _id = id;
    }

    public String getUrlThumbImage() {
        return _urlThumbImage;
    }

    public void setUrlThumbImage(String urlThumbImage) {
        _urlThumbImage = urlThumbImage;
    }

    public String getUrlThumbDetail() {
        return _urlThumbDetail;
    }

    public void setUrlThumbDetail(String urlThumbMiddle) {
        _urlThumbDetail = urlThumbMiddle;
    }

    public String getUrlDownload() {
        return _urlDownload;
    }

    public void setUrlDownload(String urlDownload) {
        _urlDownload = urlDownload;
    }

    public String getTitle() {
        return _title;
    }

    public void setTitle(String urlTitle) {
        _title = urlTitle;
    }

    public String getDescription() {
        return _description;
    }

    public void setDescription(String description) {
        _description = description;
    }

    public CameraMetaData getCameraMetaData() {
        return _cameraMetaData;
    }

    public void setCameraMetaData(CameraMetaData cameraMetaData) {
        _cameraMetaData = cameraMetaData;
    }

    public Media getMedia() {
        return _media;
    }

    public void setMedia(Media media) {
        _media = media;
    }

    public boolean isS3Available() {
        return _s3Available;
    }

    public void setS3Available(boolean s3Available) {
        _s3Available = s3Available;
    }

    public String getFileName() {
        if (!StringUtils.isEmpty(_media.getOriginalFileName())) {
            return _media.getOriginalFileName();
        }
        try {
            URI uri = URI.create(_media.getUri());
            if ("file".equals(uri.getScheme())) {
                File file = new File(uri);
                return file.getName();
            }
        } catch (Exception e) {
            // do nothing here
        }
        return null;
    }

    public String getHashValueShort() {
        return StringUtils.abbreviateMiddle(_media.getHashValue(), "..", 22);
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
