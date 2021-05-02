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
import org.pubcoi.fos.svc.models.neo.nodes.NoticeNode;
import org.springframework.data.neo4j.core.schema.RelationshipProperties;
import org.springframework.util.DigestUtils;

import java.time.ZonedDateTime;

@RelationshipEntity(Constants.Neo4J.REL_PUBLISHED)
@RelationshipProperties
public class ClientNoticeLink implements FosRelationship {

    @Id
    @org.springframework.data.neo4j.core.schema.Id
    @GeneratedValue
    @org.springframework.data.neo4j.core.schema.GeneratedValue
    Long graphId;

    String fosId;

    @EndNode
    @org.springframework.data.neo4j.core.schema.TargetNode
    NoticeNode notice;

    ZonedDateTime published;

    String transactionID;

    Boolean hidden = false;

    @StartNode // only used by OGM
    ClientNode startNode;

    public ClientNoticeLink() {}

    public ClientNoticeLink(ClientNode clientNode, NoticeNode notice, String noticeId, ZonedDateTime publishedZDT) {
        this.fosId = DigestUtils.md5DigestAsHex(String.format("%s:%s", clientNode.getFosId(), noticeId).getBytes());
        this.published = publishedZDT;
        this.notice = notice;
    }

    public String getFosId() {
        return fosId;
    }

    public ClientNoticeLink setFosId(String fosId) {
        this.fosId = fosId;
        return this;
    }

    public NoticeNode getNotice() {
        return notice;
    }

    public ClientNoticeLink setNotice(NoticeNode notice) {
        this.notice = notice;
        return this;
    }

    public ZonedDateTime getPublished() {
        return published;
    }

    public ClientNoticeLink setPublished(ZonedDateTime published) {
        this.published = published;
        return this;
    }

    public String getTransactionID() {
        return transactionID;
    }

    public ClientNoticeLink setTransactionId(String transactionID) {
        this.transactionID = transactionID;
        return this;
    }

    public Boolean getHidden() {
        return hidden;
    }

    public ClientNoticeLink setHidden(Boolean hidden) {
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

        ClientNoticeLink that = (ClientNoticeLink) o;

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
        return "ClientNoticeLink{" +
                "graphId=" + graphId +
                ", fosId='" + fosId + '\'' +
                ", published=" + published +
                '}';
    }

    public ClientNoticeLink withStartNode(ClientNode clientNode) {
        this.startNode = clientNode;
        return this;
    }
}
