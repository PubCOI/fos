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

package org.pubcoi.fos.svc.models.queries;

import org.neo4j.driver.internal.value.NodeValue;
import org.neo4j.driver.internal.value.RelationshipValue;
import org.neo4j.driver.internal.value.StringValue;

public class AwardsGraphResponse {

    StringValue clientName;
    NodeValue awardNode;
    RelationshipValue awardOrgLink;
    NodeValue awardee;
    RelationshipValue orgOrgLink;
    NodeValue legalEntity;

    AwardsGraphResponse() {}

    public NodeValue getAwardNode() {
        return awardNode;
    }

    public AwardsGraphResponse setAwardNode(NodeValue awardNode) {
        this.awardNode = awardNode;
        return this;
    }

    public RelationshipValue getAwardOrgLink() {
        return awardOrgLink;
    }

    public AwardsGraphResponse setAwardOrgLink(RelationshipValue awardOrgLink) {
        this.awardOrgLink = awardOrgLink;
        return this;
    }

    public NodeValue getAwardee() {
        return awardee;
    }

    public AwardsGraphResponse setAwardee(NodeValue awardee) {
        this.awardee = awardee;
        return this;
    }

    public RelationshipValue getOrgOrgLink() {
        return orgOrgLink;
    }

    public AwardsGraphResponse setOrgOrgLink(RelationshipValue orgOrgLink) {
        this.orgOrgLink = orgOrgLink;
        return this;
    }

    public NodeValue getLegalEntity() {
        return legalEntity;
    }

    public AwardsGraphResponse setLegalEntity(NodeValue legalEntity) {
        this.legalEntity = legalEntity;
        return this;
    }

    public StringValue getClientName() {
        return clientName;
    }

    public AwardsGraphResponse setClientName(StringValue clientName) {
        this.clientName = clientName;
        return this;
    }
}
