package org.pubcoi.fos.svc.models.dao;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.springframework.batch.core.BatchStatus;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

public class BatchJobRecentExecDAO {

    Long jobId;
    String attachmentId;
    BatchStatus status;
    OffsetDateTime startTime;
    OffsetDateTime endTime;
    List<BatchJobStepDAO> steps = new ArrayList<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        BatchJobRecentExecDAO that = (BatchJobRecentExecDAO) o;

        return new EqualsBuilder()
                .append(jobId, that.jobId)
                .append(attachmentId, that.attachmentId)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(jobId)
                .append(attachmentId)
                .toHashCode();
    }

    public String getAttachmentId() {
        return attachmentId;
    }

    public BatchJobRecentExecDAO setAttachmentId(String attachmentId) {
        this.attachmentId = attachmentId;
        return this;
    }

    public BatchStatus getStatus() {
        return status;
    }

    public BatchJobRecentExecDAO setStatus(BatchStatus status) {
        this.status = status;
        return this;
    }

    public OffsetDateTime getStartTime() {
        return startTime;
    }

    public BatchJobRecentExecDAO setStartTime(OffsetDateTime startTime) {
        this.startTime = startTime;
        return this;
    }

    public OffsetDateTime getEndTime() {
        return endTime;
    }

    public BatchJobRecentExecDAO setEndTime(OffsetDateTime endTime) {
        this.endTime = endTime;
        return this;
    }

    public List<BatchJobStepDAO> getSteps() {
        return steps;
    }

    public BatchJobRecentExecDAO setSteps(List<BatchJobStepDAO> steps) {
        this.steps = steps;
        return this;
    }

    public Long getJobId() {
        return jobId;
    }

    public BatchJobRecentExecDAO setJobId(Long jobId) {
        this.jobId = jobId;
        return this;
    }
}
