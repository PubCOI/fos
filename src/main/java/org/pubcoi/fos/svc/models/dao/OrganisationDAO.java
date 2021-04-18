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
