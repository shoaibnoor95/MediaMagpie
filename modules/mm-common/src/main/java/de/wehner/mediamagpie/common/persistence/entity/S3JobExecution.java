package de.wehner.mediamagpie.common.persistence.entity;

import javax.persistence.CascadeType;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@javax.persistence.Entity
public class S3JobExecution extends JobExecution {

    public static enum Direction {
        /**
         * put to S3
         */
        PUT,
        /**
         * Load from S3
         */
        GET
    }

    // @ManyToOne( cascade = {CascadeType.PERSIST, CascadeType.MERGE} )
    @ManyToOne(optional = false, fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinColumn(name = "MEDIA_ID")
    private Media _media;

    private Direction _direction;

    @Override
    public boolean isRetryAllowed() {
        return true;
    }

    public S3JobExecution() {
    }

    /**
     * Creates a job to upload or download a media to/from S3.
     * 
     * @param media
     *            The media that contains the original image..
     * @param direction
     *            The direction
     */
    public S3JobExecution(Media media, Direction direction) {
        this(JobStatus.QUEUED, media, direction);
    }

    public S3JobExecution(JobStatus status, Media media, Direction direction) {
        setJobStatus(status);
        setMedia(media);
        _direction = direction;
        setPriority(Priority.LOW);
    }

    public void setMedia(Media media) {
        _media = media;
    }

    public Media getMedia() {
        return _media;
    }

    public void setDirection(Direction direction) {
        _direction = direction;
    }

    public Direction getDirection() {
        return _direction;
    }
}
