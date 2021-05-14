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

package org.pubcoi.fos.svc.models.dto;

import org.pubcoi.fos.svc.models.neo.nodes.OrganisationNode;

public class OrganisationDTO {

    String fosId;
    String name;
    Boolean verified;

    OrganisationDTO() {

    }

    public OrganisationDTO(OrganisationNode organisationNode) {
        this.fosId = organisationNode.getFosId();
        this.name = organisationNode.getName();
        this.verified = organisationNode.getVerified();
    }

    public OrganisationDTO(String fosId, String name, Boolean verified) {
        this.fosId = fosId;
        this.name = name;
        this.verified = verified;
    }

    public String getFosId() {
        return fosId;
    }

    public OrganisationDTO setFosId(String fosId) {
        this.fosId = fosId;
        return this;
    }

    public String getName() {
        return name;
    }

    public OrganisationDTO setName(String name) {
        this.name = name;
        return this;
    }

    public Boolean getVerified() {
        return verified;
    }

    public OrganisationDTO setVerified(Boolean verified) {
        this.verified = verified;
        return this;
    }
}
