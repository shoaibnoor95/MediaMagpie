package de.wehner.mediamagpie.conductor.webapp.services;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import de.wehner.mediamagpie.persistence.JobExecutionDao;
import de.wehner.mediamagpie.persistence.PersistenceService;
import de.wehner.mediamagpie.persistence.TransactionHandler;
import de.wehner.mediamagpie.persistence.entity.JobExecution;
import de.wehner.mediamagpie.persistence.entity.JobStatus;

@Service
public class HousekeepingService {

    private static final Logger LOG = LoggerFactory.getLogger(HousekeepingService.class);

    private final TransactionHandler _transactionHandler;
    private final JobExecutionDao _jobExecutionDao;
    private final Semaphore _housekeepingRunning;

    @Autowired
    public HousekeepingService(PersistenceService persistenceService, TransactionHandler transactionHandler) {
        _transactionHandler = transactionHandler;
        _housekeepingRunning = new Semaphore(1);
        _jobExecutionDao = new JobExecutionDao(persistenceService);
    }

    @PreDestroy
    public void stop() throws InterruptedException {
        _housekeepingRunning.tryAcquire(1, TimeUnit.SECONDS);
    }

    @Scheduled(cron = "${mediabutler.timer.housekeeping}")
    public void onTimeTask() {
        LOG.debug("Called by timer...");
        if (_housekeepingRunning.tryAcquire()) {
            logFailedProcessedJobs();
            removeProcessedExecutionJobs();
            _housekeepingRunning.release();
        } else {
            LOG.debug("Skip housekeeping because another process always runns the housekeeping.");
        }
        LOG.debug("Called by timer...Finished");
    }

    private void logFailedProcessedJobs() {
        _transactionHandler.executeInTransaction(new Runnable() {
            @Override
            public void run() {
                List<JobExecution> jobExecutionsToRemove = _jobExecutionDao.getByStatus(
                        Arrays.asList(JobStatus.ABORTED, JobStatus.COMPLETED_WITH_WARNINGS, JobStatus.TERMINATED_WITH_ERROR), 0, 100);
                for (JobExecution jobExecution : jobExecutionsToRemove) {
                    LOG.warn("Detected failed job execution with Id=" + jobExecution.getId() + ", status=" + jobExecution.getJobStatus() + ", message='"
                            + jobExecution.getLog() + "'.");
                    _jobExecutionDao.makeTransient(jobExecution);
                }
            }
        });
    }

    private void removeProcessedExecutionJobs() {
        _transactionHandler.executeInTransaction(new Runnable() {
            @Override
            public void run() {
                List<JobExecution> jobExecutionsToRemove = _jobExecutionDao.getByStatus(
                        Arrays.asList(JobStatus.ABORTED, JobStatus.COMPLETED, JobStatus.COMPLETED_WITH_WARNINGS, JobStatus.TERMINATED_WITH_ERROR), 0, 100);
                for (JobExecution jobExecution : jobExecutionsToRemove) {
                    LOG.info("Remove JobExecution with Id=" + jobExecution.getId() + " and status=" + jobExecution.getJobStatus() + ".");
                    _jobExecutionDao.makeTransient(jobExecution);
                }
            }
        });
    }
}
