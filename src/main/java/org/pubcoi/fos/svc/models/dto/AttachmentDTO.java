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

import com.fasterxml.jackson.annotation.JsonProperty;
import org.pubcoi.cdm.cf.attachments.Attachment;
import org.pubcoi.cdm.cf.attachments.DocTypeEnum;
import org.pubcoi.cdm.cf.attachments.S3LocationType;

// corresponds to the 'additional detail' type
public class AttachmentDTO {

    @JsonProperty(required = true)
    String id;
    @JsonProperty(required = true)
    String noticeId;
    String description;
    @JsonProperty(required = true)
    String type;
    String href;
    String textData;
    String mime;
    boolean isOcr = false;

    AttachmentDTO() {}

    public AttachmentDTO(Attachment attachment) {
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

    public AttachmentDTO setId(String id) {
        this.id = id;
        return this;
    }

    public String getNoticeId() {
        return noticeId;
    }

    public AttachmentDTO setNoticeId(String noticeId) {
        this.noticeId = noticeId;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public AttachmentDTO setDescription(String description) {
        this.description = description;
        return this;
    }

    public String getType() {
        return type;
    }

    public AttachmentDTO setType(String type) {
        this.type = type;
        return this;
    }

    public String getHref() {
        return href;
    }

    public AttachmentDTO setHref(String href) {
        this.href = href;
        return this;
    }

    @Override
    public String toString() {
        return "AttachmentDTO{" +
                "id='" + id + '\'' +
                ", noticeId='" + noticeId + '\'' +
                ", type='" + type + '\'' +
                '}';
    }

    public String getTextData() {
        return textData;
    }

    public AttachmentDTO setTextData(String textData) {
        this.textData = textData;
        return this;
    }

    public String getMime() {
        return mime;
    }

    public AttachmentDTO setMime(String mime) {
        this.mime = mime;
        return this;
    }

    public boolean isOcr() {
        return isOcr;
    }

    public AttachmentDTO setOcr(boolean ocr) {
        isOcr = ocr;
        return this;
    }
}
