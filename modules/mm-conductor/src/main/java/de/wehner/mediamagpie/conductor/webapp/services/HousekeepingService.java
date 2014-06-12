package de.wehner.mediamagpie.conductor.webapp.services;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import de.wehner.mediamagpie.persistence.dao.JobExecutionDao;
import de.wehner.mediamagpie.persistence.dao.PersistenceService;
import de.wehner.mediamagpie.persistence.dao.TransactionHandler;
import de.wehner.mediamagpie.persistence.entity.JobExecution;
import de.wehner.mediamagpie.persistence.entity.JobStatus;
import de.wehner.mediamagpie.persistence.util.TimeProvider;

@Service
public class HousekeepingService {

    private static final Logger LOG = LoggerFactory.getLogger(HousekeepingService.class);

    private final TransactionHandler _transactionHandler;
    private final JobExecutionDao _jobExecutionDao;
    private final Semaphore _housekeepingRunning;
    private final TimeProvider _timeProvider;

    @Autowired
    public HousekeepingService(PersistenceService persistenceService, TransactionHandler transactionHandler, TimeProvider timeProvider) {
        _transactionHandler = transactionHandler;
        _housekeepingRunning = new Semaphore(1);
        _jobExecutionDao = new JobExecutionDao(persistenceService);
        _timeProvider = timeProvider;
    }

    @PreDestroy
    public void stop() throws InterruptedException {
        _housekeepingRunning.tryAcquire(1, TimeUnit.SECONDS);
    }

    // @Scheduled(cron = "${mediabutler.timer.housekeeping}")
    @Scheduled(fixedDelay = 5000)
    public void onTimeTask() {
        LOG.trace("Called by timer...");
        if (_housekeepingRunning.tryAcquire()) {
            try {
                removeJobsWithStatusJobs(null, JobStatus.ABORTED, JobStatus.COMPLETED_WITH_WARNINGS, JobStatus.TERMINATED_WITH_ERROR);
                // TODO rwe: add a parser to add duration which comes from configuration file
                removeJobsWithStatusJobs(new Date(_timeProvider.getTime() - (12L * 60 * 60 * 1000)), JobStatus.COMPLETED, JobStatus.COMPLETED_WITH_WARNINGS);
            } catch (Exception e) {
                LOG.error("internal error", e);
            } finally {
                // removeProcessedExecutionJobs();
                _housekeepingRunning.release();
            }
        } else {
            LOG.info("Skip housekeeping because another process always runns the housekeeping.");
        }
        LOG.trace("Called by timer...Finished");
    }

    private void removeJobsWithStatusJobs(final Date endTime, final JobStatus... jobStatus) {
        _transactionHandler.executeInTransaction(new Runnable() {
            @Override
            public void run() {
                List<JobExecution> jobExecutionsToRemove;
                if (endTime == null) {
                    jobExecutionsToRemove = _jobExecutionDao.getByStatus(Arrays.asList(jobStatus), 0, 10);
                } else {
                    jobExecutionsToRemove = _jobExecutionDao.findJobs(JobExecutionDao.JobOrder.MOST_RECENT_FIRST, endTime, jobStatus);
                }
                for (JobExecution jobExecution : jobExecutionsToRemove) {
                    LOG.info("Remove {} with Id={}, status={}, message='{}'.", jobExecution.getClass().getSimpleName(), jobExecution.getId(),
                            jobExecution.getJobStatus(), jobExecution.getLog());
                    _jobExecutionDao.makeTransient(jobExecution);
                }
            }
        });
    }
}
