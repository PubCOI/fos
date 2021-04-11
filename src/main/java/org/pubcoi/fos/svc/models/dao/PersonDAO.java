package org.pubcoi.fos.svc.models.dao;

public class PersonDAO {
    String id;
    String commonName;

    public String getId() {
        return id;
    }

    public PersonDAO setId(String id) {
        this.id = id;
        return this;
    }

    public String getCommonName() {
        return commonName;
    }

    public PersonDAO setCommonName(String commonName) {
        this.commonName = commonName;
        return this;
    }
}
