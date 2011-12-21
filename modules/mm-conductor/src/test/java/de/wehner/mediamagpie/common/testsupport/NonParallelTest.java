package de.wehner.mediamagpie.common.testsupport;

import java.util.concurrent.atomic.AtomicInteger;

import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author ralfwehner
 * @deprecated It seems that this class doen's work for JobSchedulerIntegrationTest in parallel junit test mode
 */
public class NonParallelTest extends BlockJUnit4ClassRunner {

    private static final Logger LOG = LoggerFactory.getLogger(NonParallelTest.class);
    private static final AtomicInteger _runningMethods = new AtomicInteger(0);

    public NonParallelTest(Class<?> clazz) throws InitializationError {
        super(clazz);
        if (LOG.isDebugEnabled()) {
            LOG.debug("NonParallelTest constructor called with [" + clazz + "].");
        }
    }

    @Override
    protected void runChild(FrameworkMethod method, RunNotifier notifier) {
        synchronized (_runningMethods) {
            _runningMethods.incrementAndGet();
            super.runChild(method, notifier);
            _runningMethods.decrementAndGet();
        }
    }

}
