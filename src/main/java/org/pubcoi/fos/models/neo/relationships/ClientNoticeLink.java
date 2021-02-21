package org.pubcoi.fos.models.neo.relationships;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.pubcoi.fos.models.cf.FullNotice;
import org.pubcoi.fos.models.neo.nodes.ClientNode;
import org.pubcoi.fos.models.neo.nodes.TenderNode;
import org.springframework.data.annotation.Id;
import org.springframework.data.neo4j.core.schema.RelationshipProperties;
import org.springframework.data.neo4j.core.schema.TargetNode;
import org.springframework.util.DigestUtils;

import java.time.ZonedDateTime;

@RelationshipProperties
public class ClientNoticeLink {

    @Id
    String id;

    @TargetNode
    TenderNode notice;

    ZonedDateTime published;

    String transactionID;

    Boolean hidden = false;

    public ClientNoticeLink() {}

    public ClientNoticeLink(ClientNode clientNode, FullNotice notice) {
        this.id = DigestUtils.md5DigestAsHex(String.format("%s:%s", clientNode.getId(), notice.getId()).getBytes());
        this.published = notice.getCreatedDate().toZonedDateTime();
        this.notice = new TenderNode(notice);
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

    public TenderNode getNotice() {
        return notice;
    }

    public ClientNoticeLink setNotice(TenderNode notice) {
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

    public ClientNoticeLink setTransactionID(String transactionID) {
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
