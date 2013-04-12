package de.wehner.mediamagpie.common.persistence.entity;

import javax.persistence.CascadeType;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@javax.persistence.Entity
public class CloudSyncJobExecution extends AbstractCloudJobExecution {

    public static enum CloudType {
        S3
    }

    // @ManyToOne(optional = false, cascade = { CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "USER_ID")
    @ManyToOne(optional = false)
    private User _user;

    private CloudType _cloudType;

    public CloudSyncJobExecution() {
    }

    /**
     * Creates a job to sync the local media database with external S3 etc.
     * 
     * @param user
     *            The relevant user
     * @param cloudType
     *            The external cloud type. Currently only S3 is implemented.
     */
    public CloudSyncJobExecution(User user, CloudType cloudType) {
        this(JobStatus.QUEUED, user, cloudType);
    }

    public CloudSyncJobExecution(JobStatus status, User user, CloudType cloudType) {
        setJobStatus(status);
        _user = user;
        _cloudType = cloudType;
        setPriority(Priority.LOW);
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public void setUser(User user) {
        _user = user;
    }

    public User getUser() {
        return _user;
    }

    public void setCloudType(CloudType cloudType) {
        _cloudType = cloudType;
    }

    public CloudType getCloudType() {
        return _cloudType;
    }
}
