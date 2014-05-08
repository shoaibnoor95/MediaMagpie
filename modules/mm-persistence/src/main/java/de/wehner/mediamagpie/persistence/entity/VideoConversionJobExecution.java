package de.wehner.mediamagpie.persistence.entity;

import javax.persistence.Column;

@javax.persistence.Entity
public class VideoConversionJobExecution extends JobExecution {

    @Column(name = "MEDIA_ID")
    private Long _mediaId;

    private String _destFormat;

    private Integer _widthOrHeight;

    public VideoConversionJobExecution() {
    }

    /**
     * Creates a video processing job for the given media. The job status will be set to QUEUED.
     * 
     * @param media
     *            The media that contains the original image..
     */
    public VideoConversionJobExecution(Media media, String destFormat, Integer widthOrHeight) {
        this(media, destFormat, widthOrHeight, JobStatus.QUEUED);
    }

    public VideoConversionJobExecution(Media media, String destFormat, Integer widthOrHeight, JobStatus status) {
        if (media.getId() == null) {
            throw new IllegalArgumentException("Media must have an id!");
        }
        setJobStatus(status);
        setMediaId(media.getId());
        _destFormat = destFormat;
        _widthOrHeight = widthOrHeight;
    }

    public Long getMediaId() {
        return _mediaId;
    }

    public void setMediaId(Long mediaId) {
        _mediaId = mediaId;
    }

    public String getDestFormat() {
        return _destFormat;
    }

    public void setDestFormat(String destFormat) {
        _destFormat = destFormat;
    }

    public Integer getWidthOrHeight() {
        return _widthOrHeight;
    }

    public void setWidthOrHeight(Integer widthOrHeight) {
        _widthOrHeight = widthOrHeight;
    }

}
