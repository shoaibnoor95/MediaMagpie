package de.wehner.mediamagpie.conductor.job;

import java.io.FileNotFoundException;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;

import de.wehner.mediamagpie.common.persistence.dao.MediaDao;
import de.wehner.mediamagpie.common.persistence.dao.UserConfigurationDao;
import de.wehner.mediamagpie.common.persistence.entity.JobExecution;
import de.wehner.mediamagpie.common.persistence.entity.Media;
import de.wehner.mediamagpie.common.persistence.entity.S3JobExecution;
import de.wehner.mediamagpie.common.persistence.entity.User;
import de.wehner.mediamagpie.common.persistence.entity.properties.S3Configuration;
import de.wehner.mediamagpie.conductor.media.MediaExportFactory;
import de.wehner.mediamagpie.conductor.performingjob.AbstractJob;
import de.wehner.mediamagpie.conductor.performingjob.S3PutJob;
import de.wehner.mediamagpie.persistence.PersistenceService;
import de.wehner.mediamagpie.persistence.TransactionHandler;

@Component
public class S3JobCreator extends TransactionalJobCreator<AbstractJob> {

    private final UserConfigurationDao _userConfigurationDao;
    private final MediaDao _mediaDao;

    @Autowired
    public S3JobCreator(UserConfigurationDao userConfigurationDao, MediaDao mediaDao, TransactionHandler transactionHandler,
            PersistenceService persistenceService) {
        super(transactionHandler, persistenceService);
        _userConfigurationDao = userConfigurationDao;
        _mediaDao = mediaDao;
    }

    @Override
    protected AbstractJob createInTransaction(JobExecution execution) throws FileNotFoundException {
        S3JobExecution s3JobExecution = (S3JobExecution) execution;
        Media media = s3JobExecution.getMedia();
        User user = media.getOwner();
        S3Configuration existingS3Configuration = _userConfigurationDao.getConfiguration(user, S3Configuration.class);
        if (StringUtils.isEmpty(existingS3Configuration.getAccessKey()) || StringUtils.isEmpty(existingS3Configuration.getSecretKey())) {
            throw new RuntimeException("The AWS access key or secret key is empty. Can not export to S3.");
        }
        AWSCredentials credentials = new BasicAWSCredentials(existingS3Configuration.getAccessKey(), existingS3Configuration.getSecretKey());
        MediaExportFactory mediaExportFactory = new MediaExportFactory();

        switch (s3JobExecution.getDirection()) {
        case PUT:
            return new S3PutJob(media.getOwner().getName(), credentials, mediaExportFactory.create(media), _mediaDao);
        }
        return null;
    }

    @Override
    public Class<? extends JobExecution> getJobExecutionClass() {
        return S3JobExecution.class;
    }
}
