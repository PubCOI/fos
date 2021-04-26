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

package org.pubcoi.fos.svc.models.core;

import org.pubcoi.fos.svc.models.neo.nodes.FosEntity;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.OffsetDateTime;

/**
 * Tasks are bits of work that members of the site can do
 */
@Document(collection = "fos_tasks")
public class DRTask {

    @Id
    String id;
    FosTaskType taskType;
    FosEntity entity;
    Boolean completed = false;
    FosUser completedBy;
    OffsetDateTime completedDT;

    DRTask() {}

    public DRTask(FosTaskType type, FosEntity entity) {
        this.id = String.format("%s_%s", type.toString(), entity.getFosId());
        this.taskType = type;
        this.entity = entity;
    }

    public String getId() {
        return id;
    }

    public FosTaskType getTaskType() {
        return taskType;
    }

    public DRTask setTaskType(FosTaskType taskType) {
        this.taskType = taskType;
        return this;
    }

    public FosEntity getEntity() {
        return entity;
    }

    public DRTask setEntity(FosEntity entity) {
        this.entity = entity;
        return this;
    }

    public Boolean getCompleted() {
        return completed;
    }

    public DRTask setCompleted(Boolean completed) {
        this.completed = completed;
        return this;
    }

    public FosUser getCompletedBy() {
        return completedBy;
    }

    public DRTask setCompletedBy(FosUser completedBy) {
        this.completedBy = completedBy;
        return this;
    }

    public OffsetDateTime getCompletedDT() {
        return completedDT;
    }

    public DRTask setCompletedDT(OffsetDateTime completedDT) {
        this.completedDT = completedDT;
        return this;
    }

    private DRTask setId(String id) {
        this.id = id;
        return this;
    }
}
