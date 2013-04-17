package de.wehner.mediamagpie.conductor.performingjob;

import java.net.URI;
import java.util.concurrent.Callable;

import de.wehner.mediamagpie.persistence.entity.JobExecution;

/**
 * Callable for the execution of an {@link JobExecution}. The <code>call</code> will be called from a FutureTask and does not run within a
 * transaction or <code>TransactionHandler</code>.
 */
public interface JobCallable extends Callable<URI> {

    /**
     * @return progress, 0-100%
     */
    int getProgress();

    void cancel() throws Exception;

    /**
     * Called after job is finished and runs within a <b>persistence session</b>.
     * 
     * @param result
     *            The result of the job.
     */
    void handleResult(URI result);

}
