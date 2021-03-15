package org.pubcoi.fos.svc.models.dao;

import org.pubcoi.fos.svc.models.core.DRTask;
import org.pubcoi.fos.svc.models.core.DRTaskType;

public class TaskDAO {

    String taskId;
    DRTaskType taskType;
    String entity;
    String description;

    public TaskDAO() {}

    public TaskDAO(DRTask task) {
        this.taskId = task.getId();
        this.taskType = task.getTaskType();
        this.entity = task.getEntity().getId();
    }

    public DRTaskType getTaskType() {
        return taskType;
    }

    public TaskDAO setTaskType(DRTaskType taskType) {
        this.taskType = taskType;
        return this;
    }

    public String getTaskId() {
        return taskId;
    }

    public TaskDAO setTaskId(String taskId) {
        this.taskId = taskId;
        return this;
    }

    public String getEntity() {
        return entity;
    }

    public TaskDAO setEntity(String entity) {
        this.entity = entity;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public TaskDAO setDescription(String description) {
        this.description = description;
        return this;
    }
}