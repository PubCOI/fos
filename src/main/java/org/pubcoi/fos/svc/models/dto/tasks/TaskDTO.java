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

package org.pubcoi.fos.svc.models.dto.tasks;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.pubcoi.fos.svc.models.core.DRResolvePotentialCOITask;
import org.pubcoi.fos.svc.models.core.DRTask;
import org.pubcoi.fos.svc.models.core.FosTaskType;

import java.time.OffsetDateTime;

public class TaskDTO {

    String taskId;
    FosTaskType taskType;
    String entity;
    String description;
    String linkedEntity;
    OffsetDateTime completedDT;
    boolean completed;

    public TaskDTO() {}

    public TaskDTO(DRTask task) {
        this.taskId = task.getId();
        this.taskType = task.getTaskType();
        this.entity = task.getEntity().getFosId();
        if (task instanceof DRResolvePotentialCOITask) {
            this.linkedEntity = ((DRResolvePotentialCOITask) task).getLinkedId();
        }
        this.completedDT = task.getCompletedDT();
        this.completed = task.getCompleted();
    }

    public FosTaskType getTaskType() {
        return taskType;
    }

    public TaskDTO setTaskType(FosTaskType taskType) {
        this.taskType = taskType;
        return this;
    }

    public String getTaskId() {
        return taskId;
    }

    public TaskDTO setTaskId(String taskId) {
        this.taskId = taskId;
        return this;
    }

    public String getEntity() {
        return entity;
    }

    public TaskDTO setEntity(String entity) {
        this.entity = entity;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public TaskDTO setDescription(String description) {
        this.description = description;
        return this;
    }

    public String getLinkedEntity() {
        return linkedEntity;
    }

    public TaskDTO setLinkedEntity(String linkedEntity) {
        this.linkedEntity = linkedEntity;
        return this;
    }

    public OffsetDateTime getCompletedDT() {
        return completedDT;
    }

    public boolean isCompleted() {
        return completed;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("taskId", taskId)
                .append("taskType", taskType)
                .append("entity", entity)
                .append("completed", completed)
                .toString();
    }
}
