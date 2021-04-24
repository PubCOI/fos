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

package org.pubcoi.fos.svc.services;

import org.pubcoi.cdm.cf.search.request.NoticeSearchRequest;
import org.pubcoi.cdm.cf.search.request.SearchCriteriaType;
import org.pubcoi.cdm.cf.search.response.NoticeSearchResponse;
import org.pubcoi.fos.svc.models.dto.search.NoticeSearchResponseWrapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ContractsFinderSvcImpl implements ContractsFinderSvc {

    final RestTemplate restTemplate;

    @Value("${pubcoi.fos.apis.contract-finder.search}")
    String cfSearchEndpoint;

    public ContractsFinderSvcImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public NoticeSearchResponse postSearchRequest(SearchCriteriaType searchCriteria) {
        NoticeSearchRequest searchRequest = new NoticeSearchRequest().withSearchCriteria(searchCriteria).withSize(10);
        NoticeSearchResponseWrapper searchResponse = restTemplate.postForObject(cfSearchEndpoint, searchRequest, NoticeSearchResponseWrapper.class);
        return searchResponse != null ? searchResponse.getNoticeSearchResponse() : null;
    }
}
