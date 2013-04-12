package de.wehner.mediamagpie.common.persistence.entity;

import java.util.concurrent.TimeUnit;

public abstract class AbstractCloudJobExecution extends JobExecution {

    public AbstractCloudJobExecution() {
        super();
    }

    @Override
    public Long getNextRetryTime(int retryCount) {
        switch (retryCount) {
        case 0:
            return TimeUnit.SECONDS.toMillis(1);
        case 1:
            return TimeUnit.SECONDS.toMillis(10);
        case 2:
            return TimeUnit.MINUTES.toMillis(1);
        case 3:
            return TimeUnit.MINUTES.toMillis(15);
        case 4:
            return TimeUnit.HOURS.toMillis(1);
        case 5:
            return TimeUnit.DAYS.toMillis(1);
        default:
            return null;
        }
    }

}