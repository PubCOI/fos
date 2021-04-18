/*
 * Copyright (c) 2021 PubCOI.org. This file is part of Fos@PubCOI.
 *
 * Fos@PubCOI is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Fos@PubCOI is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Fos@PubCOI.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.pubcoi.fos.svc.models.dao;

import org.pubcoi.fos.svc.models.neo.nodes.OrganisationNode;
import org.pubcoi.fos.svc.models.neo.nodes.PersonNode;

import java.util.ArrayList;
import java.util.List;

public class PersonNodeDAO {
    String id;
    String ocId;
    String commonName;
    String occupation;
    String nationality;
    List<PositionDAO> positions = new ArrayList<>();

    PersonNodeDAO() {}

    public PersonNodeDAO(PersonNode person, List<OrganisationNode> links) {
        this(person);
        for (OrganisationNode link : links) {
            positions.add(new PositionDAO(link));
        }
    }

    public PersonNodeDAO(PersonNode person) {
        this.id = person.getId();
        this.ocId = person.getOcId();
        this.commonName = person.getCommonName();
        this.occupation = person.getOccupation();
        this.nationality = person.getNationality();
    }

    public String getId() {
        return id;
    }

    public PersonNodeDAO setId(String id) {
        this.id = id;
        return this;
    }

    public String getCommonName() {
        return commonName;
    }

    public PersonNodeDAO setCommonName(String commonName) {
        this.commonName = commonName;
        return this;
    }

    public String getOcId() {
        return ocId;
    }

    public PersonNodeDAO setOcId(String ocId) {
        this.ocId = ocId;
        return this;
    }

    public String getOccupation() {
        return occupation;
    }

    public PersonNodeDAO setOccupation(String occupation) {
        this.occupation = occupation;
        return this;
    }

    public List<PositionDAO> getPositions() {
        return positions;
    }

    public PersonNodeDAO setPositions(List<PositionDAO> positions) {
        this.positions = positions;
        return this;
    }

    public String getNationality() {
        return nationality;
    }

    public PersonNodeDAO setNationality(String nationality) {
        this.nationality = nationality;
        return this;
    }
}
