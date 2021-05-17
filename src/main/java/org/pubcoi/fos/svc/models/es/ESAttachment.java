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

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.*;

import java.util.Map;

@Document(indexName = "attachments")
@Setting(settingPath = "es/attachments.json")
public class ESAttachment {

    @Field(type = FieldType.Object)
    Map<String, Object> attachment;

    @Id
    @Field(type = FieldType.Keyword)
    String id;

    @Field(name = "attachment_id", type = FieldType.Keyword)
    String attachmentId;

    @Field(type = FieldType.Text, analyzer = "attachment_analyzer", searchAnalyzer = "autocomplete_search")
    String text;

    @Field(name = "content_length", type = FieldType.Long)
    Long contentLength;

    @Field(name = "content_type", type = FieldType.Keyword)
    String contentType;

    @Field(name = "file_lang", type = FieldType.Keyword)
    String lang;

    @Field(name = "file_sha1", type = FieldType.Keyword)
    String sha1;

    @Field(name = "file_size", type = FieldType.Long)
    Long fileSize;

    @MultiField(mainField = @Field(name = "file_title", type = FieldType.Text), otherFields = {@InnerField(suffix = "keyword", type = FieldType.Keyword)})
    String title;

    @Field(name = "fos_document_type", type = FieldType.Keyword)
    String fosDocumentType;

    @Field(name = "notice_id", type = FieldType.Keyword)
    String noticeId;

    @Field(name = "page", type = FieldType.Long)
    Long pageNumber;

    @Field(name = "resource_type", type = FieldType.Keyword)
    String resourceType;

    @Field(type = FieldType.Keyword)
    String type;

    public Map<String, Object> getAttachment() {
        return attachment;
    }

    public String getAttachmentId() {
        return attachmentId;
    }

    public String getText() {
        return text;
    }

    public Long getContentLength() {
        return contentLength;
    }

    public String getContentType() {
        return contentType;
    }

    public String getLang() {
        return lang;
    }

    public String getSha1() {
        return sha1;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public String getTitle() {
        return title;
    }

    public String getFosDocumentType() {
        return fosDocumentType;
    }

    public String getNoticeId() {
        return noticeId;
    }

    public Long getPageNumber() {
        return pageNumber;
    }

    public String getResourceType() {
        return resourceType;
    }

    public String getType() {
        return type;
    }

    public String getId() {
        return id;
    }
}
