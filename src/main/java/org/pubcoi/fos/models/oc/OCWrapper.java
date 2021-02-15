package org.pubcoi.fos.models.oc;

import com.fasterxml.jackson.annotation.JsonProperty;

public class OCWrapper {
    @JsonProperty("api_version")
    String apiVersion;
    OCResults results;

    public String getApiVersion() {
        return apiVersion;
    }

    public OCWrapper setApiVersion(String apiVersion) {
        this.apiVersion = apiVersion;
        return this;
    }

    public OCResults getResults() {
        return results;
    }

    public OCWrapper setResults(OCResults results) {
        this.results = results;
        return this;
    }
}
