package de.wehner.mediamagpie.conductor.job;

import java.io.FileNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;

import de.wehner.mediamagpie.aws.s3.S3MediaExportRepository;
import de.wehner.mediamagpie.conductor.performingjob.AbstractJob;
import de.wehner.mediamagpie.conductor.performingjob.S3DeleteJob;
import de.wehner.mediamagpie.conductor.webapp.services.UploadService;
import de.wehner.mediamagpie.persistence.MediaExportFactory;
import de.wehner.mediamagpie.persistence.dao.MediaDao;
import de.wehner.mediamagpie.persistence.dao.TransactionHandler;
import de.wehner.mediamagpie.persistence.entity.CloudMediaDeleteJobExecution;
import de.wehner.mediamagpie.persistence.entity.CloudSyncJobExecution.CloudType;
import de.wehner.mediamagpie.persistence.entity.JobExecution;
import de.wehner.mediamagpie.persistence.entity.properties.S3Configuration;
import de.wehner.mediamagpie.persistence.service.ConfigurationProvider;

@Component
public class CloudMediaDeleteJobCreator extends TransactionalJobCreator<AbstractJob> {

    private final ConfigurationProvider _configurationProvider;
    private final MediaDao _mediaDao;
    private final UploadService _uploadService;
    private final MediaExportFactory _mediaExportFactory = new MediaExportFactory();

    @Autowired
    public CloudMediaDeleteJobCreator(ConfigurationProvider configurationProvider, UploadService uploadService, MediaDao mediaDao,
            TransactionHandler transactionHandler) {
        super(transactionHandler);
        _configurationProvider = configurationProvider;
        _uploadService = uploadService;
        _mediaDao = mediaDao;
    }

    @Override
    protected AbstractJob createInTransaction(JobExecution execution) throws FileNotFoundException {
        CloudMediaDeleteJobExecution cloudJobExecution = (CloudMediaDeleteJobExecution) execution;
        CloudType cloudType = cloudJobExecution.getCloudType();
        switch (cloudType) {
        case S3:
            S3Configuration existingS3Configuration = _configurationProvider.getS3Configuration(cloudJobExecution.getCloudOwner());
            if (!existingS3Configuration.hasToSyncToS3()) {
                // does the configuration has changed during the time this job was queued?
                throw new RuntimeException(
                        "The AWS access key or secret key is empty or user has disabled s3 synchronisation during job was queued. Skipt synchronization now.");
            }

            AWSCredentials credentials = new BasicAWSCredentials(existingS3Configuration.getAccessKey(), existingS3Configuration.getSecretKey());
            S3MediaExportRepository s3MediaExportRepository = new S3MediaExportRepository(credentials);
            String bucketName = cloudJobExecution.getBucketName();
            return new S3DeleteJob(bucketName, cloudJobExecution.getExportStoragePath(), cloudJobExecution.getExportStorageMetaPath(),
                    s3MediaExportRepository);
        default:
            throw new RuntimeException("Unkown cloud Type: " + cloudType);
        }

    }

    @Override
    public Class<? extends JobExecution> getJobExecutionClass() {
        return CloudMediaDeleteJobExecution.class;
    }
}
