package org.pubcoi.fos.svc.models.dao;

public class CreateTaskResponseDAO {
    String taskId;
    String message;

    public String getTaskId() {
        return taskId;
    }

    public CreateTaskResponseDAO setTaskId(String taskId) {
        this.taskId = taskId;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public CreateTaskResponseDAO setMessage(String message) {
        this.message = message;
        return this;
    }
}
