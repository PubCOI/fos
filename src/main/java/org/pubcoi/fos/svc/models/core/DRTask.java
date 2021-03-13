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
    DRTaskType taskType;
    FosEntity entity;
    Boolean completed;
    FosUser completedBy;
    OffsetDateTime completedDT;

    DRTask() {}

    public DRTask(DRTaskType type, FosEntity entity) {
        this.id = String.format("%s_%s", type.toString(), entity.getId());
        this.taskType = type;
        this.entity = entity;
    }

    public String getId() {
        return id;
    }

    public DRTaskType getTaskType() {
        return taskType;
    }

    public DRTask setTaskType(DRTaskType taskType) {
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
