package de.wehner.mediamagpie.common.persistence.entity;

import javax.persistence.CascadeType;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@javax.persistence.Entity
public class ImageResizeJobExecution extends JobExecution {

    private static final long serialVersionUID = 1L;

//    @ManyToOne( cascade = {CascadeType.PERSIST, CascadeType.MERGE} )
    @ManyToOne(optional = false, fetch=FetchType.LAZY, cascade={CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "MEDIA_ID")
    private Media _media;

    private String _label;

    @Override
    public boolean isRetryAllowed() {
        return true;
    }

    public ImageResizeJobExecution() {
    }

    /**
     * Creates a resizing job for the given media. The job status will be set to QUEUED.
     * 
     * @param media
     *            The media that contains the original image..
     */
    public ImageResizeJobExecution(Media media, String label) {
        this(JobStatus.QUEUED, media, label);
    }

    public ImageResizeJobExecution(JobStatus status, Media media, String label) {
        setJobStatus(status);
        setMedia(media);
        _label = label;
    }

    public void setMedia(Media media) {
        _media = media;
    }

    public Media getMedia() {
        return _media;
    }

    public void setLabel(String label) {
        _label = label;
    }

    public String getLabel() {
        return _label;
    }
}
