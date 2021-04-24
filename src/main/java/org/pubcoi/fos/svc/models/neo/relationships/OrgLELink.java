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

package org.pubcoi.fos.svc.models.neo.relationships;

import org.apache.commons.codec.digest.DigestUtils;
import org.pubcoi.fos.svc.models.neo.nodes.OrganisationNode;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.RelationshipProperties;
import org.springframework.data.neo4j.core.schema.TargetNode;

import java.util.ArrayList;
import java.util.List;

@RelationshipProperties
public class OrgLELink implements FosRelationship {

    @Id
    String id;

    @TargetNode
    OrganisationNode organisation;

    List<String> transactions = new ArrayList<>();

    Boolean hidden = false;

    public OrgLELink() {}

    public OrgLELink(OrganisationNode source, OrganisationNode target, String transactionId)  {
        this.id = DigestUtils.sha1Hex(String.format("%s:%s", source.getId(), target.getId()));
        this.organisation = target;
        this.transactions.add(transactionId);
    }

    public Boolean getHidden() {
        return hidden;
    }

    public OrgLELink setHidden(Boolean hidden) {
        this.hidden = hidden;
        return this;
    }

    public String getId() {
        return id;
    }

    public OrgLELink setId(String id) {
        this.id = id;
        return this;
    }

    public OrganisationNode getOrganisation() {
        return organisation;
    }

    public OrgLELink setOrganisation(OrganisationNode organisation) {
        this.organisation = organisation;
        return this;
    }

    public List<String> getTransactions() {
        return transactions;
    }

    public OrgLELink setTransactions(List<String> transactions) {
        this.transactions = transactions;
        return this;
    }
}
