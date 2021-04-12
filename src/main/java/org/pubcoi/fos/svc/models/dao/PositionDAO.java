package org.pubcoi.fos.svc.models.dao;

import org.pubcoi.fos.svc.models.neo.nodes.OrganisationNode;

public class PositionDAO {
    String companyId;
    String companyName;
    String position;

    PositionDAO() {}

    public PositionDAO(OrganisationNode link) {
        this.companyId = link.getId();
        this.companyName = link.getName();
        this.position = "undefined";
    }

    public String getCompanyId() {
        return companyId;
    }

    public PositionDAO setCompanyId(String companyId) {
        this.companyId = companyId;
        return this;
    }

    public String getCompanyName() {
        return companyName;
    }

    public PositionDAO setCompanyName(String companyName) {
        this.companyName = companyName;
        return this;
    }

    public String getPosition() {
        return position;
    }

    public PositionDAO setPosition(String position) {
        this.position = position;
        return this;
    }
}
