package org.pubcoi.fos.svc.models.core;

/**
 * Any requests from the UI that need authentication will be accompanied by a corresponding Firebase authToken: this
 * will be checked before any transactions are committed.
 * Source and target refer to graph entities that are the subject of the request.
 * Task ID refers to a "fos_task".
 */
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
