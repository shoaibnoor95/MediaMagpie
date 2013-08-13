package de.wehner.mediamagpie.persistence.entity;

import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import de.wehner.mediamagpie.api.FileNameInfo;
import de.wehner.mediamagpie.persistence.entity.CloudSyncJobExecution.CloudType;

@javax.persistence.Entity
public class CloudMediaDeleteJobExecution extends JobExecution {

    @Column(nullable = false)
    private String _bucketName;

    @Column(nullable = false)
    private String _exportStoragePath;

    @Column
    private String _exportStorageMetaPath;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_fk")
    private User _cloudOwner;

    private CloudType _cloudType;

    public CloudMediaDeleteJobExecution() {
    }

    public CloudMediaDeleteJobExecution(String bucketName, FileNameInfo fileNameInfo, CloudType cloudType, User cloudOwner) {
        this(JobStatus.QUEUED, bucketName, fileNameInfo.getNameObject(), fileNameInfo.getNameMetadata(), cloudType, cloudOwner);
    }

    public CloudMediaDeleteJobExecution(JobStatus status, String bucketName, String exportStoragePath, String exportStorageMetaPath, CloudType cloudType,
            User cloudOwner) {
        setJobStatus(status);
        _bucketName = bucketName;
        _exportStoragePath = exportStoragePath;
        _exportStorageMetaPath = exportStorageMetaPath;
        _cloudType = cloudType;
        _cloudOwner = cloudOwner;
    }

    public String getExportStoragePath() {
        return _exportStoragePath;
    }

    public void setExportStoragePath(String exportStoragePath) {
        _exportStoragePath = exportStoragePath;
    }

    public String getExportStorageMetaPath() {
        return _exportStorageMetaPath;
    }

    public void setExportStorageMetaPath(String exportStorageMetaPath) {
        _exportStorageMetaPath = exportStorageMetaPath;
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
