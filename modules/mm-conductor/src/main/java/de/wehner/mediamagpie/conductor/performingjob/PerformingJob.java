package de.wehner.mediamagpie.conductor.performingjob;

import java.net.URI;
import java.util.concurrent.Callable;

/**
 * This interface describes the classes that performs the jobs at the end. Eg: to resize an image to a smaller thumb.
 * 
 * @author ralfwehner
 * 
 */
public interface PerformingJob {

    /**
     * Initializes job. This will always be called before run gets called.
     * 
     * @param jobContext
     */
    public void init(PerformingJobContext jobContext);

    /**
     * Runs a job. This is run on the Conductor and should therefore not be CPU intensive. The main execution logic should be delegated to a
     * Hadoop cluster.
     * 
     * @return meta data about the output location.
     * 
     * @throws Exception
     */
    public URI run() throws Exception;

    /**
     * Returns a {@link Callable} which does the same as {@link #run()} on its execution.
     * 
     * @return a callable
     * @throws Exception
     */
    public JobCallable prepare() throws Exception;
}
