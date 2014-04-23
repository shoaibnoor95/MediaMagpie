package de.wehner.mediamagpie.conductor.performingjob;

import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.wehner.mediamagpie.core.util.ExceptionUtil;
import de.wehner.mediamagpie.persistence.dao.TransactionHandler;
import de.wehner.mediamagpie.persistence.entity.JobExecution;
import de.wehner.mediamagpie.persistence.entity.properties.MainConfiguration;

@Component
public class JobExecutor {

    private static final Logger LOG = LoggerFactory.getLogger(JobExecutor.class);

    private final JobFactory _jobFactory;

    @Autowired
    public JobExecutor(JobFactory dapJobFactory, TransactionHandler transactionHandler) {
        _jobFactory = dapJobFactory;
        // ThreadLocalLog4jAppender.install();
    }

    /**
     * Starts the Job locally or in a cluster environment, depending on the given configuration
     * 
     * @param mainConfiguraiton
     *            The main configuration of system
     * @param jobExecution
     * 
     * 
     * @return Meta data about the output location.
     */
    public ExecutionResult execute(final MainConfiguration mainConfiguraiton, JobExecution jobExecution) {
        try {
            JobCallable jobCallable = prepare(mainConfiguraiton, jobExecution);
            URI data = jobCallable.call();
            return new ExecutionResult(data);
        } catch (Throwable e) {
            LOG.error("Job failed.", e);
            throw ExceptionUtil.convertToRuntimeException(e);
        }
    }

    public JobCallable prepare(final MainConfiguration mainConfiguraiton, final JobExecution jobExecution) {
        return new JobCallable() {

            private JobCallable _jobCallable;
            private volatile boolean _stopFlag;

            @Override
            public URI call() throws Exception {
                PerformingJob job = _jobFactory.createPerformingJob(jobExecution);
                job.init(new PerformingJobContext(mainConfiguraiton));
                _jobCallable = job.prepare();

                if (_stopFlag) {
                    throw new IllegalStateException("job has been canceled");
                }
                URI data = _jobCallable.call();
                return data;
            }

            @Override
            public int getProgress() {
                if (_jobCallable == null) {
                    return 0;
                }
                return _jobCallable.getProgress();
            }

            @Override
            public void cancel() throws Exception {
                if (_jobCallable == null) {
                    _stopFlag = true;
                } else {
                    _jobCallable.cancel();
                }
            }

            @Override
            public void handleResult(URI result) {
                if (_jobCallable != null) {
                    _jobCallable.handleResult(result);
                }
            }
        };
    }

    public static class ExecutionResult {

        private final URI _data;

        public ExecutionResult(URI data) {
            _data = data;
        }

        public URI getData() {
            return _data;
        }
    }
}
