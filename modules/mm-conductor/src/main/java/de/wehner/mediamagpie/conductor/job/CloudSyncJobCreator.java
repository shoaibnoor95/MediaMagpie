package de.wehner.mediamagpie.conductor.job;

import java.io.FileNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;

import de.wehner.mediamagpie.aws.s3.S3MediaExportRepository;
import de.wehner.mediamagpie.conductor.performingjob.AbstractJob;
import de.wehner.mediamagpie.conductor.performingjob.S3SyncJob;
import de.wehner.mediamagpie.conductor.webapp.services.UploadService;
import de.wehner.mediamagpie.persistence.dao.MediaDao;
import de.wehner.mediamagpie.persistence.dao.PersistenceService;
import de.wehner.mediamagpie.persistence.dao.TransactionHandler;
import de.wehner.mediamagpie.persistence.entity.CloudSyncJobExecution;
import de.wehner.mediamagpie.persistence.entity.CloudSyncJobExecution.CloudType;
import de.wehner.mediamagpie.persistence.entity.JobExecution;
import de.wehner.mediamagpie.persistence.entity.User;
import de.wehner.mediamagpie.persistence.entity.properties.S3Configuration;
import de.wehner.mediamagpie.persistence.service.ConfigurationProvider;

@Component
public class CloudSyncJobCreator extends TransactionalJobCreator<AbstractJob> {

    private final ConfigurationProvider _configurationProvider;
    private final MediaDao _mediaDao;
    private final UploadService _uploadService;

    @Autowired
    public CloudSyncJobCreator(ConfigurationProvider configurationProvider, UploadService uploadService, MediaDao mediaDao,
            TransactionHandler transactionHandler, PersistenceService persistenceService) {
        super(transactionHandler, persistenceService);
        _configurationProvider = configurationProvider;
        _uploadService = uploadService;
        _mediaDao = mediaDao;
    }

    @Override
    protected AbstractJob createInTransaction(JobExecution execution) throws FileNotFoundException {
        CloudSyncJobExecution cloudSyncJobExecution = (CloudSyncJobExecution) execution;
        CloudType cloudType = cloudSyncJobExecution.getCloudType();
        User user = cloudSyncJobExecution.getUser();
        switch (cloudType) {
        case S3:
            S3Configuration existingS3Configuration = _configurationProvider.getS3Configuration(user);
            if (!existingS3Configuration.hasToSyncToS3()) {
                // does the configuration has changed during the time this job was queued?
                throw new RuntimeException(
                        "The AWS access key or secret key is empty or user has disabled s3 synchronisation during job was queued. Skipt synchronization now.");
            }

            AWSCredentials credentials = new BasicAWSCredentials(existingS3Configuration.getAccessKey(), existingS3Configuration.getSecretKey());
            switch (cloudSyncJobExecution.getCloudType()) {
            case S3:
                S3MediaExportRepository s3MediaExportRepository = new S3MediaExportRepository(credentials);
                return new S3SyncJob(s3MediaExportRepository, _uploadService, user, _configurationProvider, _transactionHandler, _mediaDao);
            }
            return null;
        default:
            throw new RuntimeException("Unkown cloud Type: " + cloudType);
        }

    }

    @Override
    public Class<? extends JobExecution> getJobExecutionClass() {
        return CloudSyncJobExecution.class;
    }
}
