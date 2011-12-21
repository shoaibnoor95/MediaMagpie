package de.wehner.mediamagpie.conductor.webapp.controller.commands;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import de.wehner.mediamagpie.common.persistence.entity.Media;

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
     * Optionally, you can add here meta information like:
     * <ul>
     * <li>Camera: NIKON D50</li>
     * <li>Resolution: 1999 x 1998</li>
     * <li>Focal Length: 0.0mm</li>
     * <li>Exposure Time: 1/10s</li>
     * <li>ISO: 200</li>
     * <li>Aperture: 0.0</li>
     * <li>Metering Mode: Center weighted average</li>
     * <li>Flash: Flash did not fire</li>
     * </ul>
     */
    private List<String> _metainformation;

    /**
     * The media entity which can be null
     * TODO rwe: check, if we really need this
     */
    private Media _media;

    public MediaThumbCommand() {
        this(null);
    }

    public MediaThumbCommand(Media media) {
        super();
        _metainformation = new ArrayList<String>();
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

    public List<String> getMetainformation() {
        return _metainformation;
    }

    public void setMetainformation(List<String> metainformation) {
        _metainformation = metainformation;
    }

    public Media getMedia() {
        return _media;
    }

    public void setMedia(Media media) {
        _media = media;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
