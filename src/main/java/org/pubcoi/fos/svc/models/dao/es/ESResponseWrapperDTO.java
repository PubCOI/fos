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

import org.elasticsearch.action.search.SearchResponse;

import java.util.ArrayList;
import java.util.List;

public class ESResponseWrapperDTO {

    Long took;

    Integer count;

    List<ESResult> results = new ArrayList<>();

    public ESResponseWrapperDTO() {

    }

    public ESResponseWrapperDTO(SearchResponse response) {
        this.took = response.getTook().getMillis();
    }

    public Long getTook() {
        return took;
    }

    public ESResponseWrapperDTO setTook(Long took) {
        this.took = took;
        return this;
    }

    public Integer getCount() {
        return count;
    }

    public ESResponseWrapperDTO setCount(Integer count) {
        this.count = count;
        return this;
    }

    public List<ESResult> getResults() {
        return results;
    }

    public ESResponseWrapperDTO setResults(List<ESResult> results) {
        this.results = results;
        return this;
    }
}
