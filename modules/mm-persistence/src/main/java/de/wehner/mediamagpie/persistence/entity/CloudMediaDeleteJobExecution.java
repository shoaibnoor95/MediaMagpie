package de.wehner.mediamagpie.persistence.entity;

@javax.persistence.Entity
public class CloudMediaDeleteJobExecution extends JobExecution {

    private Long _mediaId;

    public CloudMediaDeleteJobExecution() {
    }

    public CloudMediaDeleteJobExecution(Media media) {
        this(JobStatus.QUEUED, media);
    }

    public CloudMediaDeleteJobExecution(JobStatus status, Media media) {
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
