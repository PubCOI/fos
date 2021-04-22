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

package org.pubcoi.fos.svc.models.dto.es;

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
