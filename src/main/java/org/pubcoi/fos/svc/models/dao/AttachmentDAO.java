package org.pubcoi.fos.svc.models.dao;

import org.pubcoi.cdm.cf.attachments.Attachment;
import org.pubcoi.cdm.cf.attachments.DocTypeEnum;
import org.pubcoi.cdm.cf.attachments.S3LocationType;

// corresponds to the 'additional detail' type
public class AttachmentDAO {

    String id;
    String noticeId;
    String description;
    String type;
    String href;
    String textData;
    String mime;
    boolean isOcr = false;

    AttachmentDAO() {}

    public AttachmentDAO(Attachment attachment) {
        this.id = attachment.getId();
        this.noticeId = attachment.getNoticeId();
        this.description = attachment.getDescription();
        this.type = attachment.getDataType();
        this.href = attachment.getLink();
        this.textData = attachment.getTextData();
        this.mime = attachment.getMimeType();
        for (S3LocationType s3Location : attachment.getS3Locations()) {
            if (s3Location.getType().equals(DocTypeEnum.OCR)) {
                this.isOcr = true;
            }
        }
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

    public String getTextData() {
        return textData;
    }

    public AttachmentDAO setTextData(String textData) {
        this.textData = textData;
        return this;
    }

    public String getMime() {
        return mime;
    }

    public AttachmentDAO setMime(String mime) {
        this.mime = mime;
        return this;
    }

    public boolean isOcr() {
        return isOcr;
    }

    public AttachmentDAO setOcr(boolean ocr) {
        isOcr = ocr;
        return this;
    }
}
