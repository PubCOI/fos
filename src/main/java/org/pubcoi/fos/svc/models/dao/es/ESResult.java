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

package org.pubcoi.fos.svc.models.dao.es;

import org.pubcoi.cdm.cf.AwardDetailParentType;
import org.pubcoi.cdm.cf.FullNotice;
import org.pubcoi.cdm.cf.attachments.Attachment;
import org.springframework.util.DigestUtils;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collection;

public abstract class ESResult implements ESResultInterface {

    private static final int MAX_CHARS = 100;

    Collection<String> fragments = new ArrayList<>();
    String attachmentId;
    String noticeId;
    OffsetDateTime firstAwardDT;
    String key;
    Long hits;
    String client;
    String noticeDescription;
    OffsetDateTime noticeDT;

    public ESResult() {
    }

    public ESResult(Attachment attachment, FullNotice notice) {
        this(notice);
        this.attachmentId = attachment.getId();
        this.key = String.format("agg-%s", attachment.getId());
    }

    public ESResult(FullNotice notice) {
        this.noticeId = notice.getId();
        this.key = String.format("single-%s", notice.getId());
        this.client = notice.getNotice().getOrganisationName();
        this.noticeDescription = (null != notice.getNotice().getDescription() ?
                (notice.getNotice().getDescription().length() > MAX_CHARS ?
                        notice.getNotice().getDescription().substring(0, MAX_CHARS - 1) + "…"
                        : notice.getNotice().getDescription())
                : null);
        this.noticeDT = notice.getCreatedDate();
        for (AwardDetailParentType.AwardDetail awardDetail : notice.getAwards().getAwardDetail()) {
            if (null == this.firstAwardDT || this.firstAwardDT.isAfter(awardDetail.getAwardedDate())) {
                this.firstAwardDT = awardDetail.getAwardedDate();
            }
        }
    }

    public String getKey() {
        this.key = DigestUtils.md5DigestAsHex(String.format(
                "%s-%s", this.noticeId, this.attachmentId
        ).getBytes());
        return this.key;
    }

    public Collection<String> getFragments() {
        return fragments;
    }

    public ESResult setFragments(Collection<String> fragments) {
        this.fragments = fragments;
        return this;
    }

    public String getAttachmentId() {
        return attachmentId;
    }

    public ESResult setAttachmentId(String attachmentId) {
        this.attachmentId = attachmentId;
        return this;
    }

    public String getNoticeId() {
        return noticeId;
    }

    public ESResult setNoticeId(String noticeId) {
        this.noticeId = noticeId;
        return this;
    }

    public ESResult setKey(String key) {
        this.key = key;
        return this;
    }

    public Long getHits() {
        return hits;
    }

    public ESResult setHits(Long hits) {
        this.hits = hits;
        return this;
    }

    public String getClient() {
        return client;
    }

    public ESResult setClient(String client) {
        this.client = client;
        return this;
    }

    public String getNoticeDescription() {
        return noticeDescription;
    }

    public ESResult setNoticeDescription(String noticeDescription) {
        this.noticeDescription = noticeDescription;
        return this;
    }

    public OffsetDateTime getNoticeDT() {
        return noticeDT;
    }

    public ESResult setNoticeDT(OffsetDateTime noticeDT) {
        this.noticeDT = noticeDT;
        return this;
    }

    public OffsetDateTime getFirstAwardDT() {
        return firstAwardDT;
    }

    public ESResult setFirstAwardDT(OffsetDateTime firstAwardDT) {
        this.firstAwardDT = firstAwardDT;
        return this;
    }
}
