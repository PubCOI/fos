package org.pubcoi.fos.models.core;

import org.pubcoi.fos.models.neo.nodes.FOSEntity;
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
    FOSEntity entity;
    Boolean completed;
    FOSUser completedBy;
    OffsetDateTime completedDT;

    DRTask() {}

    public DRTask(DRTaskType type, FOSEntity entity) {
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
