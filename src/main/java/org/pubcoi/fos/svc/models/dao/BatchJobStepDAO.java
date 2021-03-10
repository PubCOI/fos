package org.pubcoi.fos.svc.models.dao;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.springframework.batch.core.BatchStatus;

import java.time.OffsetDateTime;

public class BatchJobStepDAO {

    Long id;
    String stepName;
    OffsetDateTime startTime;
    OffsetDateTime endTime;
    BatchStatus status;

    public String getStepName() {
        return stepName;
    }

    public BatchJobStepDAO setStepName(String stepName) {
        this.stepName = stepName;
        return this;
    }

    public OffsetDateTime getStartTime() {
        return startTime;
    }

    public BatchJobStepDAO setStartTime(OffsetDateTime startTime) {
        this.startTime = startTime;
        return this;
    }

    public OffsetDateTime getEndTime() {
        return endTime;
    }

    public BatchJobStepDAO setEndTime(OffsetDateTime endTime) {
        this.endTime = endTime;
        return this;
    }

    public BatchStatus getStatus() {
        return status;
    }

    public BatchJobStepDAO setStatus(BatchStatus status) {
        this.status = status;
        return this;
    }

    @Override
    public String toString() {
        return "BatchJobStepDAO{" +
                "id=" + id +
                ", status=" + status +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        BatchJobStepDAO that = (BatchJobStepDAO) o;

        return new EqualsBuilder()
                .append(id, that.id)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .toHashCode();
    }

    public Long getId() {
        return id;
    }

    public BatchJobStepDAO setId(Long id) {
        this.id = id;
        return this;
    }
}
