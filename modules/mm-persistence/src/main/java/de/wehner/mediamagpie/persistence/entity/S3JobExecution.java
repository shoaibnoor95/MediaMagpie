package de.wehner.mediamagpie.persistence.entity;

import javax.persistence.CascadeType;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@javax.persistence.Entity
public class S3JobExecution extends AbstractCloudJobExecution {

    public static enum Direction {
        /**
         * put a media to S3
         */
        PUT,
        /**
         * Load a specified media from S3 (but currently not used)
         */
        GET
    }

    // TODO rwe: this must be optional, because in some cases the media will be removed but this job still exists!
    // FIXME rwe:
    // @ManyToOne( cascade = {CascadeType.PERSIST, CascadeType.MERGE} )
    @ManyToOne(optional = false, fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinColumn(name = "MEDIA_ID")
    private Media _media;

    private Direction _direction;

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

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
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
