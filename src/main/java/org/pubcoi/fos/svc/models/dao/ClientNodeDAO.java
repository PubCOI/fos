package org.pubcoi.fos.svc.models.dao;

import org.pubcoi.fos.svc.models.neo.nodes.ClientNode;

public class ClientNodeDAO {

    String id;
    String name;
    String postCode;
    Integer tenderCount;

    public ClientNodeDAO() {}

    public ClientNodeDAO(ClientNode client) {
        this.id = client.getId();
        this.name = client.getName();
        this.postCode = client.getPostCode();
        this.tenderCount = client.getNotices().size();
    }

    public String getName() {
        return name;
    }

    public ClientNodeDAO setName(String name) {
        this.name = name;
        return this;
    }

    public String getId() {
        return id;
    }

    public ClientNodeDAO setId(String id) {
        this.id = id;
        return this;
    }

    public String getPostCode() {
        return postCode;
    }

    public ClientNodeDAO setPostCode(String postCode) {
        this.postCode = postCode;
        return this;
    }

    public Integer getTenderCount() {
        return tenderCount;
    }

    public ClientNodeDAO setTenderCount(Integer tenderCount) {
        this.tenderCount = tenderCount;
        return this;
    }
}
