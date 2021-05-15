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

package org.pubcoi.fos.svc.models.es;

import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;

@Document(indexName = "attachments", createIndex = false)
public class ESAttachment {

    String id;

    @Field(name = "file_size")
    Long fileSize;

    @Field(name = "attachment_id")
    String attachmentId;

    @Field(name = "notice_id")
    String noticeId;

    @Field(name = "page")
    Long pageNumber;

    @Field(name = "fos_document_type")
    String fosDocumentType;

    public Long getFileSize() {
        return fileSize;
    }

    public String getAttachmentId() {
        return attachmentId;
    }

    public String getNoticeId() {
        return noticeId;
    }

    public Long getPageNumber() {
        return pageNumber;
    }

    public String getFosDocumentType() {
        return fosDocumentType;
    }

    public String getId() {
        return id;
    }
}
