package de.wehner.mediamagpie.aws.s3.service;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.wehner.mediamagpie.core.concurrent.SingleThreadedController;
import de.wehner.mediamagpie.persistence.dao.CloudSyncJobExecutionDao;
import de.wehner.mediamagpie.persistence.dao.S3JobExecutionDao;
import de.wehner.mediamagpie.persistence.entity.CloudSyncJobExecution;
import de.wehner.mediamagpie.persistence.entity.JobStatus;
import de.wehner.mediamagpie.persistence.entity.Media;
import de.wehner.mediamagpie.persistence.entity.S3JobExecution;
import de.wehner.mediamagpie.persistence.entity.User;
import de.wehner.mediamagpie.persistence.util.TimeProvider;

@Service
public class S3SyncService extends SingleThreadedController {

    private static final Logger LOG = LoggerFactory.getLogger(S3SyncService.class);

    private final S3JobExecutionDao _s3JobExecutionDao;

    private final CloudSyncJobExecutionDao _cloudSyncJobExecutionDao;

    private final TimeProvider _timeProvider;

    @Autowired
    public S3SyncService(S3JobExecutionDao s3JobExecutionDao, CloudSyncJobExecutionDao cloudSyncJobExecutionDao, TimeProvider timeProvider) {
        super(TimeUnit.MINUTES, 5);
        _s3JobExecutionDao = s3JobExecutionDao;
        _cloudSyncJobExecutionDao = cloudSyncJobExecutionDao;
        _timeProvider = timeProvider;
    }

    @Override
    protected boolean execute() {
        // This will currently not used, because the user must explicitly start the synchronization.
        return false;
    }

    /**
     * Pushes only a specified Media to user's S3 bucket.
     * 
     * @param media
     */
    public void pushToS3(Media media) {
        S3JobExecution s3JobExecution = new S3JobExecution(media, S3JobExecution.Direction.PUT);
        _s3JobExecutionDao.makePersistent(s3JobExecution);
        LOG.info("Upload to S3 job for media '" + media.getId() + "' added with priority '" + s3JobExecution.getPriority() + "'.");
    }

    /**
     * Should be called when the user activates the S3 synchronization in its user settings.
     * <p>
     * Read all from S3 and sync against database.
     * </p>
     * 
     * @param user
     *            The user who wants to sync
     * @return <code>true</code> if a new sync job is queued, otherweise <code>false</code> if a sync job for requested user is already
     *         present.
     */
    public boolean syncS3Bucket(User user) {

        CloudSyncJobExecution runningJob = _cloudSyncJobExecutionDao.findS3Job(user, JobStatus.RUNNING);
        if (runningJob != null) {
            LOG.info("A sync job for requested user is currently running.");
            return false;
        }

        // test for queued jobs
        CloudSyncJobExecution waitingJob = _cloudSyncJobExecutionDao.findS3Job(user, JobStatus.QUEUED, JobStatus.RETRY);
        if (waitingJob != null) {
            LOG.info("Found a job with ID '" + waitingJob.getId() + "' and set its start time to now.");
            waitingJob.setStartTime(new Date(_timeProvider.getTime()));
            _cloudSyncJobExecutionDao.makePersistent(waitingJob);
            return false;
        }

        // add sync job for user
        CloudSyncJobExecution syncJobExecution = new CloudSyncJobExecution(user, CloudSyncJobExecution.CloudType.S3);
        _cloudSyncJobExecutionDao.makePersistent(syncJobExecution);
        LOG.info("Added new S3 sync job for user '" + user.getName() + "' with priority '" + syncJobExecution.getPriority() + "'.");
        return true;
    }

}
