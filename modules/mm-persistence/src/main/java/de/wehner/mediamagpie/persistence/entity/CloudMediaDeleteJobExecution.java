package de.wehner.mediamagpie.persistence.entity;

import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import de.wehner.mediamagpie.persistence.entity.CloudSyncJobExecution.CloudType;

@javax.persistence.Entity
public class CloudMediaDeleteJobExecution extends JobExecution {

    @Column(nullable = false)
    private String _bucketName;

    @Column(nullable = false)
    private String _exportStoragePath;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_fk")
    private User _cloudOwner;

    private CloudType _cloudType;

    public CloudMediaDeleteJobExecution() {
    }

    public CloudMediaDeleteJobExecution(String bucketName, String exportStoragePath, CloudType cloudType, User cloudOwner) {
        this(JobStatus.QUEUED, bucketName, exportStoragePath, cloudType, cloudOwner);
    }

    public CloudMediaDeleteJobExecution(JobStatus status, String bucketName, String exportStoragePath, CloudType cloudType, User cloudOwner) {
        setJobStatus(status);
        _bucketName = bucketName;
        _exportStoragePath = exportStoragePath;
        _cloudType = cloudType;
        _cloudOwner = cloudOwner;
    }

    public String getExportStoragePath() {
        return _exportStoragePath;
    }

    public void setExportStoragePath(String exportStoragePath) {
        _exportStoragePath = exportStoragePath;
    }

    public User getCloudOwner() {
        return _cloudOwner;
    }

    public void setCloudOwner(User cloudOwner) {
        this._cloudOwner = cloudOwner;
    }

    public void setBucketName(String bucketName) {
        _bucketName = bucketName;
    }

    public String getBucketName() {
        return _bucketName;
    }

    public CloudType getCloudType() {
        return _cloudType;
    }

    public void setCloudType(CloudType cloudType) {
        _cloudType = cloudType;
    }
}
