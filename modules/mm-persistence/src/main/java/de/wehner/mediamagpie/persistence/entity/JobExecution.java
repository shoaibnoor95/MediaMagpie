package de.wehner.mediamagpie.persistence.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Lob;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import de.wehner.mediamagpie.core.util.ExceptionUtil;

/**
 * Base class for all jobs that must be stored into db. It uses the strategy <code>InheritanceType.JOINED</code>.
 * <p>
 * In comparision to <i>SpringBatch</i> concepts this is a mixture of <code>JobInstance</code> and <code>JobExecution</code>.
 * </p>
 * 
 * @param <T>
 *            The subclass of this Job which is at now: StreamJob, FeedJob or FountainJob.
 */
@javax.persistence.Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class JobExecution extends Base implements Cloneable {

    private String _description;
    @Column(nullable = false)
    private JobStatus _jobStatus;
    private Date _startTime;
    private Date _stopTime;
    @Lob
    private String _log;
    @Column(nullable = false)
    private Priority _priority = Priority.NORMAL;

    private String _createdDataUri;

    private int _retryCount;

    /**
     * This method controls whether or not a new retry job will start and if so when it starts.
     * 
     * @param retryCount
     *            The restart attempts before. This is zero if the previous jobs fails the first time.
     * @return The waiting time in milli seconds the next retry job will start. When <code>null</code> is returned no more retry job will be
     *         started..
     */
    public Long getNextRetryTime(int retryCount) {
        return ((retryCount <= 2) ? 0L : null);
    }

    public JobExecution() {
        _retryCount = 0;
    }

    public String getDescription() {
        return _description;
    }

    public void setDescription(String description) {
        this._description = description;
    }

    public JobStatus getJobStatus() {
        return _jobStatus;
    }

    public void setJobStatus(JobStatus status) {
        _jobStatus = status;
    }

    public Date getStartTime() {
        return _startTime;
    }

    public void setStartTime(Date startTime) {
        _startTime = startTime;
    }

    public Date getStopTime() {
        return _stopTime;
    }

    public void setStopTime(Date endTime) {
        _stopTime = endTime;
    }

    public String getLog() {
        return _log;
    }

    public void setLog(String log) {
        _log = log;
    }

    public Priority getPriority() {
        return _priority;
    }

    public void setPriority(Priority priority) {
        _priority = priority;
    }

    public void setCreatedDataUri(String createdData) {
        _createdDataUri = createdData;
    }

    public String getCreatedDataUri() {
        return _createdDataUri;
    }

    public int getRetryCount() {
        return _retryCount;
    }

    public void setRetryCount(int retryCount) {
        _retryCount = retryCount;
    }

    public JobExecution makeOfflineCopy() {
        try {
            JobExecution offlineCopy = (JobExecution) super.clone();
            return offlineCopy;
        } catch (CloneNotSupportedException e) {
            throw ExceptionUtil.convertToRuntimeException(e);
        }
    }

    @Override
    public final String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).append("id", getId()).toString();
    }
}
