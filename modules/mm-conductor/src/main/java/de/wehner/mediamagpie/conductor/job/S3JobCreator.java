package de.wehner.mediamagpie.conductor.job;

import java.io.FileNotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;

import de.wehner.mediamagpie.conductor.performingjob.AbstractJob;
import de.wehner.mediamagpie.conductor.performingjob.S3PutJob;
import de.wehner.mediamagpie.persistence.MediaExportFactory;
import de.wehner.mediamagpie.persistence.dao.MediaDao;
import de.wehner.mediamagpie.persistence.dao.TransactionHandler;
import de.wehner.mediamagpie.persistence.dao.UserConfigurationDao;
import de.wehner.mediamagpie.persistence.entity.JobExecution;
import de.wehner.mediamagpie.persistence.entity.Media;
import de.wehner.mediamagpie.persistence.entity.S3JobExecution;
import de.wehner.mediamagpie.persistence.entity.User;
import de.wehner.mediamagpie.persistence.entity.properties.S3Configuration;

@Component
public class S3JobCreator extends TransactionalJobCreator<AbstractJob> {

    private final static Logger LOG = LoggerFactory.getLogger(S3JobCreator.class);

    private final UserConfigurationDao _userConfigurationDao;
    private final MediaDao _mediaDao;

    @Autowired
    public S3JobCreator(UserConfigurationDao userConfigurationDao, MediaDao mediaDao, TransactionHandler transactionHandler) {
        super(transactionHandler);
        _userConfigurationDao = userConfigurationDao;
        _mediaDao = mediaDao;
    }

    @Override
    protected AbstractJob createInTransaction(JobExecution execution) throws FileNotFoundException {
        S3JobExecution s3JobExecution = (S3JobExecution) execution;
        Media media = _mediaDao.getById(s3JobExecution.getMediaId());
        if (media == null) {
            // this is possible in case of the media is deleted by a MediaDeleteJob before
            LOG.warn("Can not find media with id {}. Maybe the media was deleted before this job was executed.", s3JobExecution.getMediaId());
            return null;
        }
        User user = media.getOwner();
        S3Configuration existingS3Configuration = _userConfigurationDao.getConfiguration(user, S3Configuration.class);
        if (!existingS3Configuration.hasToSyncToS3()) {
            // does the configuration has changed during the time this job was queued?
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
