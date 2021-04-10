package org.pubcoi.fos.svc.models.dao.es;

import org.elasticsearch.search.SearchHit;
import org.pubcoi.cdm.cf.FullNotice;
import org.springframework.util.DigestUtils;

import java.util.Arrays;

public class ESSingleResponseDTO extends ESResult {

    Integer pageNumber;

    public ESSingleResponseDTO(SearchHit hit) {
        if (null != hit.getHighlightFields().get("content")) {
            Arrays.stream(hit.getHighlightFields().get("content").getFragments())
                    .forEach(content -> this.fragments.add(content.toString()));
        }
    }

    public ESSingleResponseDTO(FullNotice notice) {
        super(notice);
    }

    public String getKey() {
        this.key = DigestUtils.md5DigestAsHex(String.format(
                "%s-%s-%s", this.noticeId, this.attachmentId, this.pageNumber
        ).getBytes());
        return this.key;
    }

    public Integer getPageNumber() {
        return pageNumber;
    }

    public ESSingleResponseDTO setPageNumber(Integer pageNumber) {
        this.pageNumber = pageNumber;
        return this;
    }
}
