package de.wehner.mediamagpie.conductor.webapp.util;

import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.wehner.mediamagpie.conductor.webapp.controller.ImageController;

public class TimeoutExecutor {

    private static final Logger LOG = LoggerFactory.getLogger(ImageController.class);

    private final long timeout;

    private final long sleeping;

    public TimeoutExecutor(long timeout, long sleeping) {
        this.timeout = timeout;
        this.sleeping = sleeping;
    }

    public void checkUntilConditionIsTrue(Callable<Boolean> callable) {
        long end = System.currentTimeMillis() + timeout;
        try {
            while (System.currentTimeMillis() < end) {
                if (callable.call() == true) {
                    break;
                }
                Thread.sleep(sleeping);
            }
            LOG.warn("Skip condition testing due to timeout!");
        } catch (InterruptedException e) {
            LOG.warn("stop checking conditions", e);
        } catch (Exception e) {
            LOG.warn("Got exception during checking the condition.", e);
        }
    }

    public <T> T callUntilReturnIsNotNull(Callable<T> callable) {
        long end = System.currentTimeMillis() + timeout;
        try {
            while (System.currentTimeMillis() < end) {
                T result = callable.call();
                if (result != null) {
                    return result;
                }
                Thread.sleep(sleeping);
            }
        } catch (InterruptedException e) {
            LOG.warn("stop checking conditions", e);
        } catch (Exception e) {
            LOG.warn("Got exception during checking the condition.", e);
        }
        return null;
    }
}
// public class TimeoutExecutor {
//
// public static final Logger LOG = LoggerFactory.getLogger(ImageController.class);
// private final TimeProvider _timeProvider;
//
// public TimeoutExecutor() {
// this(null);
// }
//
// public TimeoutExecutor(TimeProvider timeProvider) {
// if (timeProvider == null) {
// _timeProvider = new TimeProvider();
// } else {
// _timeProvider = timeProvider;
// }
// }
//
// public void checkUntilConditionIsTrue(long timeout, long sleeping, Callable<Boolean> callable) {
// long end = _timeProvider.getTime() + timeout;
// try {
// while (_timeProvider.getTime() < end) {
// if (callable.call() == true) {
// break;
// }
// Thread.sleep(sleeping);
// }
// } catch (InterruptedException e) {
// LOG.warn("stop checking conditions", e);
// } catch (Exception e) {
// LOG.warn("Got exception during checking the condition.", e);
// }
// }
// }
