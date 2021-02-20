package org.pubcoi.fos.models.core;

public class RequestWithAuth {

    String authToken;
    String taskID;
    String source;
    String target;

    public String getAuthToken() {
        return authToken;
    }

    public RequestWithAuth setAuthToken(String authToken) {
        this.authToken = authToken;
        return this;
    }

    public String getSource() {
        return source;
    }

    public RequestWithAuth setSource(String source) {
        this.source = source;
        return this;
    }

    public String getTarget() {
        return target;
    }

    public RequestWithAuth setTarget(String target) {
        this.target = target;
        return this;
    }

    public String getTaskID() {
        return taskID;
    }

    public RequestWithAuth setTaskID(String taskID) {
        this.taskID = taskID;
        return this;
    }
}
