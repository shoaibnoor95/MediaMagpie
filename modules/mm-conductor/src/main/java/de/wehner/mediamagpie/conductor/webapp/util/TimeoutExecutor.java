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

    public boolean checkUntilConditionIsTrue(Callable<Boolean> callable) {
        long end = System.currentTimeMillis() + timeout;
        try {
            while (System.currentTimeMillis() < end) {
                if (callable.call() == true) {
                    return true;
                }
                Thread.sleep(sleeping);
            }
            LOG.warn("Skip condition testing due to timeout!");
        } catch (InterruptedException e) {
            LOG.warn("stop checking conditions", e);
        } catch (Exception e) {
            LOG.warn("Got exception during checking the condition.", e);
        }
        return false;
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
