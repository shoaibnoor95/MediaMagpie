package de.wehner.mediamagpie.persistence.entity;

import javax.persistence.CascadeType;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@javax.persistence.Entity
public class VideoConversionJobExecution extends JobExecution {

    @ManyToOne(optional = false, fetch=FetchType.LAZY, cascade={CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "MEDIA_ID")
    private Media _media;

    private String _label;

    public VideoConversionJobExecution() {
    }

    /**
     * Creates a video processing job for the given media. The job status will be set to QUEUED.
     * 
     * @param media
     *            The media that contains the original image..
     */
    public VideoConversionJobExecution(Media media, String label) {
        this(JobStatus.QUEUED, media, label);
    }

    public VideoConversionJobExecution(JobStatus status, Media media, String label) {
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
