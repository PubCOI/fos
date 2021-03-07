package org.pubcoi.fos.models.dao;

import org.elasticsearch.search.SearchHit;
import org.springframework.util.DigestUtils;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

public class ESResponseDTO {
    Collection<String> fragments = new ArrayList<>();
    String attachmentId;
    String noticeId;
    Integer pageNumber;
    String key;
    String organisation;
    String noticeDescription;
    OffsetDateTime noticeDT;

    public ESResponseDTO() {}

    public ESResponseDTO(SearchHit hit) {
        if (null != hit.getHighlightFields().get("content")) {
            Arrays.stream(hit.getHighlightFields().get("content").getFragments())
                    .forEach(content -> this.fragments.add(content.toString()));
        }
    }

    public Collection<String> getFragments() {
        return fragments;
    }

    public ESResponseDTO setFragments(Collection<String> fragments) {
        this.fragments = fragments;
        return this;
    }

    public String getAttachmentId() {
        return attachmentId;
    }

    public ESResponseDTO setAttachmentId(String attachmentId) {
        this.attachmentId = attachmentId;
        return this;
    }

    public String getNoticeId() {
        return noticeId;
    }

    public ESResponseDTO setNoticeId(String noticeId) {
        this.noticeId = noticeId;
        return this;
    }

    public Integer getPageNumber() {
        return pageNumber;
    }

    public ESResponseDTO setPageNumber(Integer pageNumber) {
        this.pageNumber = pageNumber;
        return this;
    }

    public String getKey() {
        this.key = DigestUtils.md5DigestAsHex(String.format(
                "%s-%s-%s", this.noticeId, this.attachmentId, this.pageNumber
        ).getBytes());
        return this.key;
    }

    public OffsetDateTime getNoticeDT() {
        return noticeDT;
    }

    public ESResponseDTO setNoticeDT(OffsetDateTime noticeDT) {
        this.noticeDT = noticeDT;
        return this;
    }

    public String getOrganisation() {
        return organisation;
    }

    public ESResponseDTO setOrganisation(String organisation) {
        this.organisation = organisation;
        return this;
    }

    public String getNoticeDescription() {
        return noticeDescription;
    }

    public ESResponseDTO setNoticeDescription(String noticeDescription) {
        this.noticeDescription = noticeDescription;
        return this;
    }
}
