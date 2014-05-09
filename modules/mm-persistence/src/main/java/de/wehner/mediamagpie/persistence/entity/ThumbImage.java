package de.wehner.mediamagpie.persistence.entity;

import java.io.File;

import javax.persistence.CascadeType;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import de.wehner.mediamagpie.persistence.realms.ThumbImageRelation;

@javax.persistence.Entity
@EntityListeners({ ThumbImageRelation.class })
@NamedQueries( { @NamedQuery(name = "getByMediaIdAndLabel", query = "select t from ThumbImage as t where t._label = :label and _media._id = :mediaId") })
public class ThumbImage extends CreationDateBase {

    @ManyToOne(optional = false, fetch=FetchType.LAZY, cascade={CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "MEDIA_ID")
    private Media _media;

    private String _label;

    private String _pathToImage;

    public ThumbImage() {
        // default constructor
    }

    public ThumbImage(Media media) {
        this(media, null, new File(media.getUri()).getPath());
    }

    public ThumbImage(Media media, String label, String pathToImage) {
        _media = media;
        _label = label;
        _pathToImage = pathToImage;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
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
}
