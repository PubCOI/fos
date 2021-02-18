package org.pubcoi.fos.models.neo.relationships;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.pubcoi.fos.models.neo.nodes.NoticeNode;
import org.springframework.data.annotation.Id;
import org.springframework.data.neo4j.core.schema.RelationshipProperties;
import org.springframework.data.neo4j.core.schema.TargetNode;

@RelationshipProperties
public class ClientNoticeLink {

    @Id
    String id;

    @TargetNode
    NoticeNode notice;

    public ClientNoticeLink() {}

    public ClientNoticeLink(NoticeNode noticeNode) {
        this.notice = noticeNode;
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
}
