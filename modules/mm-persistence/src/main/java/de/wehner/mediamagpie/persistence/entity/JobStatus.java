package de.wehner.mediamagpie.persistence.entity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum JobStatus {

    /** Job has been queued for processing. (0) */
    QUEUED(false, false, false, "Queued"),

    /** Job has been requested to be restarted. (1) */
    RETRY(false, false, false, "Retry"),

    /** Job has been sent to a processing node, but processing hasn't started, yet. (2) */
    SENT_TO_PROCESSING_NODE(false, false, false, "Sent to Processing Node"),

    /** Job is currently running. (3) */
    RUNNING(false, false, false, "Running"),

    /** Job was aborted. (4) */
    ABORTED(true, false, true, "Aborted"),

    /** Job completed successfully. (5) */
    COMPLETED(true, true, false, "Completed"),

    /** Job terminated with an error. (6) */
    TERMINATED_WITH_ERROR(true, false, true, "Error"),

    /** Job completed with warning(s). */
    COMPLETED_WITH_WARNINGS(true, true, false, "Completed With Warnings"),

    /** Job was stopped though was already running on a processing node **/
    STOPPING(false, false, true, "Stopping");

    public static final List<JobStatus> STATI_ON_PROCESSING_NODE = Arrays.asList(SENT_TO_PROCESSING_NODE, RUNNING);
    public static final List<JobStatus> STATI_BUSY = Arrays.asList(QUEUED, SENT_TO_PROCESSING_NODE, RUNNING);

    private boolean _finished;
    private boolean _triggersDependentJobs;
    private boolean _error;
    private String _name;

    private JobStatus(boolean finished, boolean triggersDependentJobs, boolean error, String name) {
        _finished = finished;
        _triggersDependentJobs = triggersDependentJobs;
        _error = error;
        _name = name;
    }

    public boolean isFinished() {
        return _finished;
    }

    public boolean isError() {
        return _error;
    }

    public String getName() {
        return _name;
    }

    public boolean isTriggersDependentJobs() {
        return _triggersDependentJobs;
    }

    public static List<JobStatus> getBusyStati() {
        List<JobStatus> busyStati = new ArrayList<JobStatus>();
        for (JobStatus jobStatus : values()) {
            if (!jobStatus.isFinished()) {
                busyStati.add(jobStatus);
            }
        }
        return busyStati;
    }
}
