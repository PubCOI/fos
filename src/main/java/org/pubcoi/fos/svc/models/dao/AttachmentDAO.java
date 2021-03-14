package org.pubcoi.fos.svc.models.dao;

import org.pubcoi.fos.cdm.attachments.Attachment;
import org.pubcoi.fos.models.cf.AdditionalDetailsType;

// corresponds to the 'additional detail' type
public class AttachmentDAO {

    String id;
    String noticeId;
    String description;
    String type;
    String href;

    AttachmentDAO() {}

    public AttachmentDAO(AdditionalDetailsType details) {
        this.id = details.getId();
        this.noticeId = details.getNoticeId();
        this.description = details.getDescription();
        this.type = details.getDataType();
        this.href = details.getLink();
    }

    public AttachmentDAO(Attachment attachment) {
        this.id = attachment.getId();
        this.noticeId = attachment.getNoticeId();
        this.description = attachment.getDescription();
        this.type = attachment.getDataType();
        this.href = attachment.getLink();
    }

    public String getId() {
        return id;
    }

    public AttachmentDAO setId(String id) {
        this.id = id;
        return this;
    }

    public String getNoticeId() {
        return noticeId;
    }

    public AttachmentDAO setNoticeId(String noticeId) {
        this.noticeId = noticeId;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public AttachmentDAO setDescription(String description) {
        this.description = description;
        return this;
    }

    public String getType() {
        return type;
    }

    public AttachmentDAO setType(String type) {
        this.type = type;
        return this;
    }

    public String getHref() {
        return href;
    }

    public AttachmentDAO setHref(String href) {
        this.href = href;
        return this;
    }

    @Override
    public String toString() {
        return "AttachmentDAO{" +
                "id='" + id + '\'' +
                ", noticeId='" + noticeId + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
