package org.pubcoi.fos.models.core;

import org.pubcoi.fos.models.neo.nodes.FOSEntity;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.ZonedDateTime;

@Document(collection = "fos_tasks")
public class DRTask {

    @Id
    String taskID;
    DRTaskType taskType;
    FOSEntity entity;
    Boolean completed;
    FOSUser completedBy;
    ZonedDateTime completedDT;

    DRTask() {}

    public DRTask(DRTaskType type, FOSEntity entity) {
        this.taskID = String.format("%s_%s", type.toString(), entity.getId());
        this.taskType = type;
        this.entity = entity;
    }

    public String getTaskID() {
        return taskID;
    }

    public DRTaskType getTaskType() {
        return taskType;
    }

    public DRTask setTaskType(DRTaskType taskType) {
        this.taskType = taskType;
        return this;
    }

    public FOSEntity getEntity() {
        return entity;
    }

    public DRTask setEntity(FOSEntity entity) {
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

    public FOSUser getCompletedBy() {
        return completedBy;
    }

    public DRTask setCompletedBy(FOSUser completedBy) {
        this.completedBy = completedBy;
        return this;
    }

    public ZonedDateTime getCompletedDT() {
        return completedDT;
    }

    public DRTask setCompletedDT(ZonedDateTime completedDT) {
        this.completedDT = completedDT;
        return this;
    }

    private DRTask setTaskID(String taskID) {
        this.taskID = taskID;
        return this;
    }
}
