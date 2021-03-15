package org.pubcoi.fos.svc.models.dao;

public class CreateTaskRequestDAO {
    String type;
    String id;

    public String getType() {
        return type;
    }

    public CreateTaskRequestDAO setType(String type) {
        this.type = type;
        return this;
    }

    public String getId() {
        return id;
    }

    public CreateTaskRequestDAO setId(String id) {
        this.id = id;
        return this;
    }
}
