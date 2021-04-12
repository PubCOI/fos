package org.pubcoi.fos.svc.models.dao;

import org.pubcoi.fos.svc.models.neo.nodes.OrganisationNode;
import org.pubcoi.fos.svc.models.neo.nodes.PersonNode;

import java.util.ArrayList;
import java.util.List;

public class PersonDAO {
    String id;
    String ocId;
    String commonName;
    String occupation;
    String nationality;
    List<PositionDAO> positions = new ArrayList<>();

    PersonDAO() {}

    public PersonDAO(PersonNode person, List<OrganisationNode> links) {
        this.id = person.getId();
        this.ocId = person.getOcId();
        this.commonName = person.getCommonName();
        this.occupation = person.getOccupation();
        this.nationality = person.getNationality();
        for (OrganisationNode link : links) {
            positions.add(new PositionDAO(link));
        }
    }

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

    public String getOcId() {
        return ocId;
    }

    public PersonDAO setOcId(String ocId) {
        this.ocId = ocId;
        return this;
    }

    public String getOccupation() {
        return occupation;
    }

    public PersonDAO setOccupation(String occupation) {
        this.occupation = occupation;
        return this;
    }

    public List<PositionDAO> getPositions() {
        return positions;
    }

    public PersonDAO setPositions(List<PositionDAO> positions) {
        this.positions = positions;
        return this;
    }

    public String getNationality() {
        return nationality;
    }

    public PersonDAO setNationality(String nationality) {
        this.nationality = nationality;
        return this;
    }
}
