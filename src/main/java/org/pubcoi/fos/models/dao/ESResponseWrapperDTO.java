package org.pubcoi.fos.models.dao;

import java.util.ArrayList;
import java.util.List;

public class ESResponseWrapperDTO {

    Long tookInMillis;

    List<ESResponseDTO> results = new ArrayList<>();

    public List<ESResponseDTO> getResults() {
        return results;
    }

    public ESResponseWrapperDTO setResults(List<ESResponseDTO> results) {
        this.results = results;
        return this;
    }

    public Long getTookInMillis() {
        return tookInMillis;
    }

    public ESResponseWrapperDTO setTookInMillis(Long tookInMillis) {
        this.tookInMillis = tookInMillis;
        return this;
    }
}
