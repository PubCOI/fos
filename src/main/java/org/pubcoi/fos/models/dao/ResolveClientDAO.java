package org.pubcoi.fos.models.dao;

import org.pubcoi.fos.models.neo.nodes.ClientNode;

public class ResolveClientDAO {

    String id;
    String clientName;
    Boolean isCanonical;
    String canonicalID;
    String postCode;

    public ResolveClientDAO() {}

    public ResolveClientDAO(ClientNode clientNode) {
        this.id = clientNode.getId();
        this.clientName = clientNode.getClientName();
        this.isCanonical = clientNode.getCanonical();
        this.canonicalID = (null != clientNode.getClient()) ? clientNode.getClient().getId() : null;
        this.postCode = clientNode.getPostCode();
    }

    public String getId() {
        return id;
    }

    public ResolveClientDAO setId(String id) {
        this.id = id;
        return this;
    }

    public String getClientName() {
        return clientName;
    }

    public ResolveClientDAO setClientName(String clientName) {
        this.clientName = clientName;
        return this;
    }

    public Boolean getCanonical() {
        return isCanonical;
    }

    public String getCanonicalID() {
        return canonicalID;
    }

    public ResolveClientDAO setCanonical(Boolean canonical) {
        isCanonical = canonical;
        return this;
    }

    public ResolveClientDAO setCanonicalID(String canonicalID) {
        this.canonicalID = canonicalID;
        return this;
    }

    public String getPostCode() {
        return postCode;
    }

    public ResolveClientDAO setPostCode(String postCode) {
        this.postCode = postCode;
        return this;
    }
}
