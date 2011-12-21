package de.wehner.mediamagpie.conductor.performingjob;

import java.net.URI;

public abstract class AbstractJob implements PerformingJob {

    private PerformingJobContext _jobContext;

    @Override
    public final void init(PerformingJobContext jobContext) {
        _jobContext = jobContext;
        onInit();
    }

    protected void onInit() {
        // subclasses may override
    }

    @Override
    public final URI run() throws Exception {
        return prepare().call();
    }

    public PerformingJobContext getPerformingJobContext() {
        return _jobContext;
    }
}
