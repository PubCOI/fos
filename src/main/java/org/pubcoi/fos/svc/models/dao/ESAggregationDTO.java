package org.pubcoi.fos.svc.models.dao;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

public class ESAggregationDTO {

    String attachmentId;
    String noticeId;
    String key;
    Long hits;
    String organisation;
    String noticeDescription;
    OffsetDateTime noticeDT;
    List<String> fragments = new ArrayList<>();

    public String getAttachmentId() {
        return attachmentId;
    }

    public ESAggregationDTO setAttachmentId(String attachmentId) {
        this.attachmentId = attachmentId;
        return this;
    }

    public String getNoticeId() {
        return noticeId;
    }

    public ESAggregationDTO setNoticeId(String noticeId) {
        this.noticeId = noticeId;
        return this;
    }

    public String getKey() {
        return key;
    }

    public ESAggregationDTO setKey(String key) {
        this.key = key;
        return this;
    }

    public String getOrganisation() {
        return organisation;
    }

    public ESAggregationDTO setOrganisation(String organisation) {
        this.organisation = organisation;
        return this;
    }

    public String getNoticeDescription() {
        return noticeDescription;
    }

    public ESAggregationDTO setNoticeDescription(String noticeDescription) {
        this.noticeDescription = noticeDescription;
        return this;
    }

    public OffsetDateTime getNoticeDT() {
        return noticeDT;
    }

    public ESAggregationDTO setNoticeDT(OffsetDateTime noticeDT) {
        this.noticeDT = noticeDT;
        return this;
    }

    public Long getHits() {
        return hits;
    }

    public ESAggregationDTO setHits(Long hits) {
        this.hits = hits;
        return this;
    }

    public List<String> getFragments() {
        return fragments;
    }

    public ESAggregationDTO setFragments(List<String> fragments) {
        this.fragments = fragments;
        return this;
    }
}
