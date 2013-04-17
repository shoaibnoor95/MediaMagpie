package de.wehner.mediamagpie.conductor.performingjob;

import de.wehner.mediamagpie.persistence.entity.JobExecution;

public interface JobCreator {

    public PerformingJob create(JobExecution execution);

    public Class<? extends JobExecution> getJobExecutionClass();
}
