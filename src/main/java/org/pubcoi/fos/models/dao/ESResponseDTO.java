package org.pubcoi.fos.models.dao;

import org.elasticsearch.search.SearchHit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

public class ESResponseDTO {
    Collection<String> fragments = new ArrayList<>();
    String attachmentId;
    String noticeId;
    Integer pageNumber;

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
}
