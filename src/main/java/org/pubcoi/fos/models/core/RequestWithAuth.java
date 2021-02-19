package org.pubcoi.fos.models.core;

public class RequestWithAuth {

    String authToken;

    public String getAuthToken() {
        return authToken;
    }

    public RequestWithAuth setAuthToken(String authToken) {
        this.authToken = authToken;
        return this;
    }
}
