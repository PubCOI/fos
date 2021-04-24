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
import org.pubcoi.cdm.cf.FullNotice;
import org.pubcoi.fos.svc.models.neo.nodes.ClientNode;
import org.pubcoi.fos.svc.models.neo.nodes.NoticeNode;
import org.springframework.data.annotation.Id;
import org.springframework.data.neo4j.core.schema.RelationshipProperties;
import org.springframework.data.neo4j.core.schema.TargetNode;
import org.springframework.util.DigestUtils;

import java.time.ZonedDateTime;

@RelationshipProperties
public class ClientNoticeLink implements FosRelationship {

    @Id
    String id;

    @TargetNode
    NoticeNode notice;

    ZonedDateTime published;

    String transactionID;

    Boolean hidden = false;

    public ClientNoticeLink() {}

    public ClientNoticeLink(ClientNode clientNode, FullNotice notice) {
        this.id = DigestUtils.md5DigestAsHex(String.format("%s:%s", clientNode.getId(), notice.getId()).getBytes());
        this.published = notice.getCreatedDate().toZonedDateTime();
        this.notice = new NoticeNode(notice);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        ClientNoticeLink that = (ClientNoticeLink) o;

        return new EqualsBuilder()
                .append(id, that.id)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .toHashCode();
    }

    public String getId() {
        return id;
    }

    public ClientNoticeLink setId(String id) {
        this.id = id;
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
}
