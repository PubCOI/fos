/*
 * Copyright (c) 2021 PubCOI.org. This file is part of Fos@PubCOI.
 *
 * Fos@PubCOI is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Fos@PubCOI is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Fos@PubCOI.  If not, see <https://www.gnu.org/licenses/>.
 */

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
