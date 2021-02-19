package org.pubcoi.fos.models.dao;

import org.pubcoi.fos.models.core.DRTask;
import org.pubcoi.fos.models.core.DRTaskType;

public class TaskDAO {

    String taskID;
    DRTaskType taskType;
    String entity;
    String description;

    public TaskDAO() {}

    public TaskDAO(DRTask task) {
        this.taskID = task.getTaskID();
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

    public String getTaskID() {
        return taskID;
    }

    public TaskDAO setTaskID(String taskID) {
        this.taskID = taskID;
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
