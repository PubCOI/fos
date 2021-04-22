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

import org.pubcoi.fos.svc.models.core.DRTask;
import org.pubcoi.fos.svc.models.core.FosTaskType;

public class TaskDTO {

    String taskId;
    FosTaskType taskType;
    String entity;
    String description;

    public TaskDTO() {}

    public TaskDTO(DRTask task) {
        this.taskId = task.getId();
        this.taskType = task.getTaskType();
        this.entity = task.getEntity().getId();
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
}
