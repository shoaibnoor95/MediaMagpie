package de.wehner.mediamagpie.conductor.job;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.wehner.mediamagpie.common.util.Holder;

public abstract class SingleThreadedController {

    private static final Logger LOG = LoggerFactory.getLogger(SingleThreadedController.class);

    private ExecutorService _executorService;
    private long _waitTimeMs;
    private final Holder<Boolean> _terminateFlag = new Holder<Boolean>(Boolean.FALSE);

    public SingleThreadedController() {
        this(TimeUnit.MILLISECONDS, 250);
    }

    public SingleThreadedController(TimeUnit timeUnit, long waitTime) {
        _waitTimeMs = timeUnit.toMillis(waitTime);
    }

    @PostConstruct
    public void start() {
        LOG.info("Starting controller " + getClass().getName());
        _executorService = Executors.newFixedThreadPool(1);
        _executorService.execute(new Runnable() {

            @Override
            public void run() {
                loop();
            }
        });
    }

    void loop() {
        boolean _shouldSleep = false;
        try {
            while (!Thread.currentThread().isInterrupted()) {
                if (_shouldSleep) {
                    if (_terminateFlag.get()) {
                        LOG.info("received terminate flag and shutdown loop() method in " + getClass().getSimpleName() + ".");
                        break;
                    }
                    Thread.sleep(_waitTimeMs);
                    _shouldSleep = false;
                }
                try {
                    if (!execute()) {
                        _shouldSleep = true;
                    }
                } catch (Throwable t) {
                    LOG.error("Error occurred.", t);
                    _shouldSleep = true;
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        LOG.info("Controller thread terminating.");
    }

    /**
     * Controller execution.
     * 
     * @return <code>false</code> if there is nothing to do and the controller should fall asleep for some time. <code>true</code> if
     *         controller should call this immediately again.
     */
    protected abstract boolean execute();

    @PreDestroy
    public void stop() throws InterruptedException {
        _terminateFlag.set(Boolean.TRUE);
        if (_executorService != null && !_executorService.isTerminated()) {
            LOG.info("Stopping controller " + getClass().getName());
            _executorService.shutdownNow();
            _executorService.awaitTermination(20, TimeUnit.SECONDS);
        }
    }
}
