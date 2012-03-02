package de.wehner.mediamagpie.conductor.performingjob;

import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.wehner.mediamagpie.common.persistence.entity.JobExecution;
import de.wehner.mediamagpie.common.persistence.entity.properties.MainConfiguration;
import de.wehner.mediamagpie.common.util.ExceptionUtil;
import de.wehner.mediamagpie.conductor.persistence.TransactionHandler;


@Component
public class JobExecutor {

    private static final Logger LOG = LoggerFactory.getLogger(JobExecutor.class);

    private final JobFactory _dapJobFactory;

    @Autowired
    public JobExecutor(JobFactory dapJobFactory, TransactionHandler transactionHandler) {
        _dapJobFactory = dapJobFactory;
        // ThreadLocalLog4jAppender.install();
    }

    /**
     * TODO rwe: clean up DAP specific code
     * Starts the Job locally or in a cluster environment, depending on the given execution engine.
     * 
     * @param jobExecution
     *            The job which should be started.
     * @param executionEngine
     *            the engine for mr jobs
     * 
     * @return Meta data about the output location.
     * @deprecated Currently not used.
     */
    public ExecutionResult execute(final MainConfiguration mainConfiguraiton, JobExecution jobExecution) {
        try {
            JobCallable jobCallable = prepare(mainConfiguraiton, jobExecution);
            URI data = jobCallable.call();
            return new ExecutionResult(data/* , jobCallable.getDapJobCounter() */);
        } catch (Throwable e) {
            LOG.error("Job failed.", e);
            throw ExceptionUtil.convertToRuntimeException(e);
        }
    }

    public JobCallable prepare(final MainConfiguration mainConfiguraiton, final JobExecution dapJobExecution) {
        return new JobCallable() {

            private JobCallable _jobCallable;
            private volatile boolean _stopFlag;

            @Override
            public URI call() throws Exception {
                // LoggerWriter loggerWriter = null;
                // DapFilesystem dapFilesystem = _dapFilesystemProvider.createDapFilesystem();
                // try {
                // executionEngine.getFileSystem().delete(new
                // Path(dapFilesystem.getTempDirectory(dapJobExecution)), true);
                // loggerWriter = new
                // FileLoggerWriter(dapFilesystem.getJobLogFile(dapJobExecution.getId()));
                // ThreadLocalLog4jAppender.addLogger(loggerWriter);
                // JobConf jobConf = executionEngine.newJobConf();
                // DapJobConfiguration dapJobConfiguration =
                // dapJobExecution.getDapJobConfiguration();
                PerformingJob job = _dapJobFactory.createDapJob(/* dapJobConfiguration, */dapJobExecution);
//                LOG.warn("############ really try to init job (" + job.getClass().getName() + "), no mock! ############");
                job.init(new PerformingJobContext(mainConfiguraiton));
                // LOG.debug("Starting job. (class='" + job.getClass().getName() + "', id='" +
                // dapJobExecution.getId() + "')");
                _jobCallable = job.prepare();

                if (_stopFlag) {
                    throw new IllegalStateException("job has been canceled");
                }
                URI data = _jobCallable.call();
                // dapJobExecution.getDapJobCounter().putAll(getDapJobCounter());
                return data;
                // } catch (Exception e) {
                // LOG.error("Job failed.", e);
                // throw e;
                // } finally {
                // try {
                // executionEngine.getFileSystem().delete(new
                // Path(dapFilesystem.getTempDirectory(dapJobExecution)), true);
                // } catch (Exception e2) {
                // LOG.warn("failed to clean the tmp directory for job execution " +
                // dapJobExecution, e2);
                // }
                // ThreadLocalLog4jAppender.removeLogger(loggerWriter);
                // }
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

        // private final Map<DapJobCounter, Long> _counters;

        // public ExecutionResult(Data data, Map<DapJobCounter, Long> counter) {
        // _data = data;
        // _counters = counter;
        // }

        public ExecutionResult(URI data) {
            _data = data;
        }

        public URI getData() {
            return _data;
        }

        // public Map<DapJobCounter, Long> getCounters() {
        // return _counters;
        // }
    }
}