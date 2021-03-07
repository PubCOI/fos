package org.pubcoi.fos.models.dao;

import org.elasticsearch.action.search.SearchResponse;

import java.util.ArrayList;
import java.util.List;

public class ESResponseWrapperDTO {

    Long tookInMillis;

    Integer results;

    List<ESResponseDTO> paged = new ArrayList<>();

    List<ESAggregationDTO> aggregated = new ArrayList<>();

    public ESResponseWrapperDTO() {

    }

    public ESResponseWrapperDTO(SearchResponse response) {
        this.tookInMillis = response.getTook().getMillis();
    }

    public List<ESResponseDTO> getPaged() {
        return paged;
    }

    public ESResponseWrapperDTO setPaged(List<ESResponseDTO> paged) {
        this.paged = paged;
        return this;
    }

    public Long getTookInMillis() {
        return tookInMillis;
    }

    public ESResponseWrapperDTO setTookInMillis(Long tookInMillis) {
        this.tookInMillis = tookInMillis;
        return this;
    }

    public List<ESAggregationDTO> getAggregated() {
        return aggregated;
    }

    public ESResponseWrapperDTO setAggregated(List<ESAggregationDTO> aggregated) {
        this.aggregated = aggregated;
        return this;
    }

    public Integer getResults() {
        return results;
    }

    public ESResponseWrapperDTO setResults(Integer results) {
        this.results = results;
        return this;
    }
}
