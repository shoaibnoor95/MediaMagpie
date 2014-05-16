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

import de.wehner.mediamagpie.persistence.realms.ConvertedVideoRelation;

@javax.persistence.Entity
@EntityListeners({ ConvertedVideoRelation.class })
// rwe: remove me: @NamedQueries({ @NamedQuery(name = "getByMediaIdAndLabelAndFormat", query = "select v from ConvertedVideo as v where v._label = :label and _media._id = :mediaId and v._videoFormat = :videoFormat") })
public class ConvertedVideo extends CreationDateBase {

    @ManyToOne(optional = false, fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinColumn(name = "MEDIA_ID")
    private Media _media;

    /**
     * can be 'original' or the width of converted video
     */
    private String _label;

    /**
     * currently the file extension like 'webm' or 'mp4'
     */
    private String _videoFormat;

    private String _pathToFile;

    public ConvertedVideo() {
        // default constructor
    }

    /**
     * @param media
     * @param label
     *            The width of converted video or 'original' ({@link VideoService#ORIGINAL_SIZE})
     * @param videoFormat
     * @param pathToFile
     */
    public ConvertedVideo(Media media, String label, String videoFormat, String pathToFile) {
        _media = media;
        _label = label;
        _videoFormat = videoFormat;
        _pathToFile = pathToFile;
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

    public String getPathToFile() {
        return _pathToFile;
    }

    public void setPathToFile(String pathToFile) {
        _pathToFile = pathToFile;
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
