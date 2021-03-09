package org.pubcoi.fos.svc.models.core;

public class SearchRequestDAO {

    String q;
    String type;
    Boolean groupResults;

    public String getQ() {
        return q;
    }

    public SearchRequestDAO setQ(String q) {
        this.q = q;
        return this;
    }

    public String getType() {
        return type;
    }

    public SearchRequestDAO setType(String type) {
        this.type = type;
        return this;
    }

    public Boolean getGroupResults() {
        return groupResults;
    }

    public SearchRequestDAO setGroupResults(Boolean groupResults) {
        this.groupResults = groupResults;
        return this;
    }
}
