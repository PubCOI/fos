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
