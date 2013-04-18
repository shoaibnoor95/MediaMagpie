package de.wehner.mediamagpie.conductor.webapp.services;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.wehner.mediamagpie.conductor.job.SingleThreadedTransactionController;
import de.wehner.mediamagpie.conductor.performingjob.JobCallable;
import de.wehner.mediamagpie.conductor.performingjob.JobExecutor;
import de.wehner.mediamagpie.core.util.ExceptionUtil;
import de.wehner.mediamagpie.persistence.dao.ConfigurationDao;
import de.wehner.mediamagpie.persistence.dao.JobExecutionDao;
import de.wehner.mediamagpie.persistence.dao.TransactionHandler;
import de.wehner.mediamagpie.persistence.entity.JobExecution;
import de.wehner.mediamagpie.persistence.entity.JobStatus;
import de.wehner.mediamagpie.persistence.entity.properties.MainConfiguration;
import de.wehner.mediamagpie.persistence.util.TimeProvider;

@Service
public class JobScheduler extends SingleThreadedTransactionController {

    private static final Logger LOG = LoggerFactory.getLogger(JobScheduler.class);

    private final JobExecutionDao _jobExecutionDao;
    private final ConfigurationDao _configurationDao;
    private final JobExecutor _jobExecutor;
    private final ExecutorService _executorService;
    private final TimeProvider _timeProvider;
    private final Map<Long, Future<URI>> _runningJobFutureByJobId = new ConcurrentHashMap<Long, Future<URI>>();
    private final Map<Long, JobCallable> _runningJobCallableByJobId = new ConcurrentHashMap<Long, JobCallable>();

    @Autowired
    public JobScheduler(TransactionHandler transactionHandler, JobExecutionDao jobExecutionDao, ConfigurationDao configurationDao,
            JobExecutor jobExecutor, TimeProvider timeProvider) {
        this(transactionHandler, jobExecutionDao, configurationDao, jobExecutor, timeProvider, Executors.newFixedThreadPool(4));
    }

    public JobScheduler(TransactionHandler transactionHandler, JobExecutionDao jobExecutionDao, ConfigurationDao configurationDao,
            JobExecutor jobExecutor, TimeProvider timeProvider, ExecutorService executorService) {
        super(transactionHandler);
        _jobExecutionDao = jobExecutionDao;
        _configurationDao = configurationDao;
        _jobExecutor = jobExecutor;
        _executorService = executorService;
        _timeProvider = timeProvider;
    }

    @Override
    public void stop() throws InterruptedException {
        super.stop();
        _executorService.shutdownNow();
        _executorService.awaitTermination(10, TimeUnit.SECONDS);
    }

    @Override
    public void start() {
        resetRunningJobsToQueued();
        super.start();
    }

    @Override
    protected boolean executeInTransaction() {
        if (collectFinishedJobs()) {
            return true;
        }

        List<JobExecution> jobsToStart = getJobsToStart(0, 1);
        if (!jobsToStart.isEmpty()) {
            JobExecution jobToStart = jobsToStart.get(0);
            try {
                startJob(jobToStart);
            } catch (Throwable e) {
                handleJobException(jobToStart, e);
            }
            return true;
        }

        return false;
    }

    /**
     * @param startIndex
     *            First job to return.
     * @param numberOfJobs
     *            Max number of jobs to return.
     * @return the next jobs that should be started.
     */
    private List<JobExecution> getJobsToStart(int startIndex, int numberOfJobs) {
        return _jobExecutionDao.getByStatus(Arrays.asList(JobStatus.QUEUED), startIndex, numberOfJobs);
    }

    /**
     * Starts a job.
     * 
     * @param jobExecution
     *            The job to start.
     */
    private void startJob(JobExecution jobExecution) {
        jobExecution.setJobStatus(JobStatus.RUNNING);
        jobExecution.setStartTime(new Date());

        final JobExecution offlineJobExecution = jobExecution.makeOfflineCopy();
        MainConfiguration mainConfiguration = _configurationDao.getConfiguration(MainConfiguration.class);
        JobCallable jobCallable = _jobExecutor.prepare(mainConfiguration, offlineJobExecution);
        _runningJobCallableByJobId.put(jobExecution.getId(), jobCallable);
        _runningJobFutureByJobId.put(jobExecution.getId(), _executorService.submit(jobCallable));
    }

    private void resetRunningJobsToQueued() {
        List<JobExecution> jobs;
        do {
            jobs = getTransactionHandler().executeInTransaction(new Callable<List<JobExecution>>() {
                @Override
                public List<JobExecution> call() throws Exception {
                    List<JobExecution> jobs = _jobExecutionDao.getByStatus(Arrays.asList(JobStatus.RUNNING), 0, 100);
                    for (JobExecution runningJobExecution : jobs) {
                        runningJobExecution.setJobStatus(JobStatus.QUEUED);
                        runningJobExecution.setStartTime(null);
                    }
                    return jobs;
                }
            });
        } while (!jobs.isEmpty());
    }

    private boolean collectFinishedJobs() {
        for (Entry<Long, Future<URI>> entry : _runningJobFutureByJobId.entrySet()) {
            Future<URI> future = entry.getValue();
            if (future.isDone()) {
                Long dapJobExecutionId = entry.getKey();
                JobExecution jobExecution = _jobExecutionDao.getById(dapJobExecutionId);
                try {
                    URI resultData = future.get();
                    JobCallable jobCallable = _runningJobCallableByJobId.get(jobExecution.getId());
                    JobStatus jobStatus = extractJobStatus(new ArrayList<String>()/* errorLogs */);
                    jobExecution.setJobStatus(jobStatus);
                    jobExecution.setCreatedDataUri((resultData != null) ? resultData.toASCIIString() : null);
                    completeJob(jobExecution);
                    jobCallable.handleResult(resultData);
                    LOG.info("Job " + jobExecution + " completed with status " + jobStatus + ".");
                } catch (RuntimeException e) {
                    handleJobException(jobExecution, e);
                } catch (ExecutionException e) {
                    handleJobException(jobExecution, e.getCause());
                } catch (InterruptedException e) {
                    ExceptionUtil.retainInterruptFlag(e);
                }
                if (jobExecution != null) {
                    jobExecution.setStopTime(new Date());
                }
                _runningJobFutureByJobId.remove(dapJobExecutionId);
                _runningJobCallableByJobId.remove(dapJobExecutionId);
                return true;
            }
        }
        return false;
    }

    private void handleJobException(JobExecution jobExecution, Throwable throwable) {
        LOG.warn("Job '" + jobExecution + "' terminated with error.", throwable);
        if (jobExecution == null) {
            return;
        }
        jobExecution.setStopTime(new Date());
        Long retryWaitingTime = jobExecution.getNextRetryTime(jobExecution.getRetryCount());
        if (retryWaitingTime != null) {
            jobExecution.setRetryCount(jobExecution.getRetryCount() + 1);
            jobExecution.setStartTime(new Date(_timeProvider.getTime() + retryWaitingTime));
            jobExecution.setJobStatus(JobStatus.QUEUED);
            LOG.info("requeueing '" + jobExecution + "' with new status '" + jobExecution.getJobStatus() + "' and waitingTime '"
                    + retryWaitingTime + "'(ms).");
        } else {
            LOG.warn("Set job '" + jobExecution + "' to new status '" + JobStatus.TERMINATED_WITH_ERROR + "' caused by multiple errors.");
            jobExecution.setLog(throwable.getMessage());
            jobExecution.setJobStatus(JobStatus.TERMINATED_WITH_ERROR);
        }
        completeJob(jobExecution);
    }

    private void completeJob(JobExecution jobExecution) {
    }

    private JobStatus extractJobStatus(List<String> errorLogs) {
        if (!errorLogs.isEmpty()) {
            return JobStatus.COMPLETED_WITH_WARNINGS;
        }
        return JobStatus.COMPLETED;
    }

    public ExecutorService getExecutorService() {
        return _executorService;
    }

    public Set<Long> getRunningJobs() {
        return _runningJobFutureByJobId.keySet();
    }

    public Map<Long, JobCallable> getRunningJobCallables() {
        return _runningJobCallableByJobId;
    }

}
