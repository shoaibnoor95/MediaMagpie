package de.wehner.mediamagpie.conductor.performingjob;

import java.io.File;

import de.wehner.mediamagpie.common.fs.ApplicationFS;
import de.wehner.mediamagpie.common.persistence.entity.properties.MainConfiguration;


public class PerformingJobContext {

    // private final DapContext _dapContext;
    // private final JobConf _jobConf;
    // private final DapJobExecution _dapJobExecution;
    // private final ExecutionEngine _executionEngine;
    private final MainConfiguration _mainConfiguration;
    // private final UserConfiguration _userConfiguration;
    // private final UserApplicationFsView _applicationFs;
    private final ApplicationFS _applicationFS;

    // public DapJobContext(DapContext dapContext, ExecutionEngine executionEngine, DapJobExecution dapJobExecution, JobConf jobConf) {
    // _dapContext = dapContext;
    // _executionEngine = executionEngine;
    // _dapJobExecution = dapJobExecution;
    // _jobConf = jobConf;
    // }

    public PerformingJobContext(MainConfiguration mainConfiguration/* , UserConfiguration userConfiguration, User user */) {
        _mainConfiguration = mainConfiguration;
        // _userConfiguration = userConfiguration;
        _applicationFS = new ApplicationFS(new File(mainConfiguration.getTempMediaPath()));
    }

    // public DapContext getDapContext() {
    // return _dapContext;
    // }
    //
    // public JobConf getJobConf() {
    // return _jobConf;
    // }
    //
    // public DapJobExecution getDapJobExecution() {
    // return _dapJobExecution;
    // }
    //
    // public ExecutionEngine getExecutionEngine() {
    // return _executionEngine;
    // }
    //
    // public DapFilesystem getDapFilesystem() {
    // return _dapContext.getDapFilesystem();
    // }

    public MainConfiguration getMainConfiguration() {
        return _mainConfiguration;
    }

    public ApplicationFS getApplicationFS() {
        return _applicationFS;
    }
}
