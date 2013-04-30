package de.wehner.mediamagpie.persistence.entity;

@javax.persistence.Entity
public class MediaDeleteJobExecution extends JobExecution {

    /**
     * This is just a reference by id because the media entity must be removed from db although the causing MediaDeleteJobExecution still
     * remains a little time in db after it will be removed by house keeping.
     */
    private Long _mediaId;

    public MediaDeleteJobExecution() {
    }

    public MediaDeleteJobExecution(Media media) {
        this(JobStatus.QUEUED, media);
    }

    public MediaDeleteJobExecution(JobStatus status, Media media) {
        setJobStatus(status);
        _mediaId = media.getId();
    }

    public void setMediaId(long mediaId) {
        _mediaId = mediaId;
    }

    public long getMediaId() {
        return _mediaId;
    }

}
