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

import org.pubcoi.cdm.cf.FullNotice;
import org.pubcoi.cdm.cf.search.request.NoticeSearchRequest;
import org.pubcoi.cdm.cf.search.request.SearchCriteriaType;
import org.pubcoi.cdm.cf.search.response.NoticeHitType;
import org.pubcoi.cdm.cf.search.response.NoticeSearchResponse;
import org.pubcoi.fos.svc.exceptions.FosBadRequestException;
import org.pubcoi.fos.svc.models.dto.search.NoticeSearchResponseWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ContractsFinderSvcImpl implements ContractsFinderSvc {
    private static final Logger logger = LoggerFactory.getLogger(ContractsFinderSvcImpl.class);

    final RestTemplate restTemplate;
    final NoticesSvc noticesSvc;
    final XslSvc xslSvc;

    @Value("${pubcoi.fos.apis.contract-finder.search}")
    String cfSearchEndpoint;

    @Value("${pubcoi.fos.apis.contract-finder.get-notice}")
    String cfGetNoticeEndpoint;

    public ContractsFinderSvcImpl(RestTemplate restTemplate, NoticesSvc noticesSvc, XslSvc xslSvc) {
        this.restTemplate = restTemplate;
        this.noticesSvc = noticesSvc;
        this.xslSvc = xslSvc;
    }

    @Override
    public NoticeSearchResponse postSearchRequest(SearchCriteriaType searchCriteria) {
        NoticeSearchRequest searchRequest = new NoticeSearchRequest().withSearchCriteria(searchCriteria).withSize(10);
        NoticeSearchResponseWrapper searchResponse = restTemplate.postForObject(cfSearchEndpoint, searchRequest, NoticeSearchResponseWrapper.class);
        if (searchResponse != null) {
            for (NoticeHitType hitOfNoticeIndex : searchResponse.getNoticeSearchResponse().getNoticeList().getHitOfNoticeIndices()) {
                hitOfNoticeIndex.getItem().setAlreadyLoaded(noticesSvc.exists(hitOfNoticeIndex.getItem().getId()));
            }
            return searchResponse.getNoticeSearchResponse();
        }
        return null;
    }

    @Override
    public FullNotice addNotice(String noticeId) {
        if (noticesSvc.exists(noticeId)) {
            return noticesSvc.getNotice(noticeId);
        }
        else {
            logger.info("Requesting notice {} from remote endpoint", noticeId);
            // nb notice has bunch of different namespaces on it ... doing the naughty thing and just nuking them for now
            String noticeStr = restTemplate.getForObject(String.format(cfGetNoticeEndpoint, noticeId), String.class);
            FullNotice notice = xslSvc.cleanGetNoticeResponse(noticeStr);
            if (null == notice || null == noticeId) throw new FosBadRequestException("Unable to get notice");
            return noticesSvc.addNotice(notice);
        }
    }
}
