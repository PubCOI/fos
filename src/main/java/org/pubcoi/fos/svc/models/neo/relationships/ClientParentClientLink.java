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

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.neo4j.ogm.annotation.*;
import org.pubcoi.fos.svc.models.core.Constants;
import org.pubcoi.fos.svc.models.neo.nodes.ClientNode;
import org.pubcoi.fos.svc.transactions.FosTransaction;
import org.springframework.data.neo4j.core.schema.RelationshipProperties;
import org.springframework.data.neo4j.core.schema.TargetNode;
import org.springframework.util.DigestUtils;

import java.time.ZonedDateTime;

@RelationshipEntity(Constants.Neo4J.REL_AKA)
@RelationshipProperties
public class ClientParentClientLink implements FosRelationship {

    @Id
    @org.springframework.data.neo4j.core.schema.Id
    @GeneratedValue
    @org.springframework.data.neo4j.core.schema.GeneratedValue
    Long graphId;

    String fosId;

    @StartNode // only used by OGM
    ClientNode startNode;

    @EndNode
    @TargetNode
    ClientNode client;

    ZonedDateTime transactionDT;

    String transactionID;

    Boolean hidden = false;

    ClientParentClientLink() {}

    public ClientParentClientLink(ClientNode source, ClientNode target, FosTransaction transaction) {
        this.startNode = source;
        this.fosId = DigestUtils.md5DigestAsHex(String.format("%s:%s", transaction.getId(), target.getFosId()).getBytes());
        this.transactionID = transaction.getId();
        this.transactionDT = transaction.getTransactionDT().toZonedDateTime();
        this.client = target;
    }

    public String getFosId() {
        return fosId;
    }

    public ClientNode getClient() {
        return client;
    }

    public ZonedDateTime getTransactionDT() {
        return transactionDT;
    }

    public String getTransactionID() {
        return transactionID;
    }

    public Boolean getHidden() {
        return hidden;
    }

    public ClientParentClientLink setHidden(Boolean hidden) {
        this.hidden = hidden;
        return this;
    }

    @Override
    public Long getGraphId() {
        return graphId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        ClientParentClientLink that = (ClientParentClientLink) o;

        return new EqualsBuilder()
                .append(graphId, that.graphId)
                .append(fosId, that.fosId)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(graphId)
                .append(fosId)
                .toHashCode();
    }

    @Override
    public String toString() {
        return "ClientParentClientLink{" +
                "graphId=" + graphId +
                ", fosId='" + fosId + '\'' +
                '}';
    }
}
