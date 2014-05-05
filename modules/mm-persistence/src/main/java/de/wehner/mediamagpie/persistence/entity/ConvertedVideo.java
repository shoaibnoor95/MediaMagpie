package de.wehner.mediamagpie.persistence.entity;

import javax.persistence.CascadeType;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

@javax.persistence.Entity
@EntityListeners({ ThumbImageRelation.class })
@NamedQueries({ @NamedQuery(name = "getByMediaIdAndLabelAndFormat", query = "select v from ConvertedVideo as v where v._label = :label and _media._id = :mediaId and v._videoFormat = :videoFormat") })
public class ConvertedVideo extends CreationDateBase {

    @ManyToOne(optional = false, fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinColumn(name = "MEDIA_ID")
    private Media _media;

    private String _label;

    private String _videoFormat;

    private String _pathToImage;

    public ConvertedVideo() {
        // default constructor
    }

    public ConvertedVideo(Media media, String label, String videoFormat, String pathToImage) {
        _media = media;
        _label = label;
        _videoFormat = videoFormat;
        _pathToImage = pathToImage;
    }

    public Media getMedia() {
        return _media;
    }

    public void setMedia(Media media) {
        _media = media;
    }

    public String getLabel() {
        return _label;
    }

    public void setLabel(String label) {
        _label = label;
    }

    public String getPathToImage() {
        return _pathToImage;
    }

    public void setPathToImage(String pathToImage) {
        _pathToImage = pathToImage;
    }

    public String getVideoFormat() {
        return _videoFormat;
    }

    public void setVideoFormat(String videoFormat) {
        this._videoFormat = videoFormat;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
