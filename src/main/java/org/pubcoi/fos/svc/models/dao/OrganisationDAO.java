package org.pubcoi.fos.svc.models.dao;

import org.pubcoi.fos.svc.models.neo.nodes.OrganisationNode;

public class OrganisationDAO {

    String id;
    String name;
    Boolean verified;

    OrganisationDAO() {

    }
    public OrganisationDAO(OrganisationNode organisationNode) {
        this.id = organisationNode.getId();
        this.name = organisationNode.getName();
        this.verified = organisationNode.getVerified();
    }

    public String getId() {
        return id;
    }

    public OrganisationDAO setId(String id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public OrganisationDAO setName(String name) {
        this.name = name;
        return this;
    }

    public Boolean getVerified() {
        return verified;
    }

    public OrganisationDAO setVerified(Boolean verified) {
        this.verified = verified;
        return this;
    }
}
