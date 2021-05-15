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

package org.pubcoi.fos.svc.rest;

import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.unit.Fuzziness;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.metrics.ParsedTopHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.pubcoi.cdm.cf.FullNotice;
import org.pubcoi.cdm.cf.attachments.Attachment;
import org.pubcoi.cdm.cf.base.NoticeStatusEnum;
import org.pubcoi.cdm.cf.search.request.SearchCriteriaType;
import org.pubcoi.cdm.cf.search.response.NoticeSearchResponse;
import org.pubcoi.cdm.fos.FosESFields;
import org.pubcoi.fos.svc.exceptions.endpoint.FosEndpointException;
import org.pubcoi.fos.svc.models.core.SearchRequestDTO;
import org.pubcoi.fos.svc.models.dto.es.*;
import org.pubcoi.fos.svc.models.dto.search.ContractFinderSearchRequestDTO;
import org.pubcoi.fos.svc.models.dto.search.SearchByDateTypeEnum;
import org.pubcoi.fos.svc.models.es.MemberInterest;
import org.pubcoi.fos.svc.repos.mdb.AttachmentMDBRepo;
import org.pubcoi.fos.svc.repos.mdb.MnisMembersRepo;
import org.pubcoi.fos.svc.repos.mdb.NoticesMDBRepo;
import org.pubcoi.fos.svc.services.ContractsFinderSvc;
import org.pubcoi.fos.views.FosViews;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.Optional;

@RestController
public class SearchRest {
    private static final Logger logger = LoggerFactory.getLogger(SearchRest.class);

    final AttachmentMDBRepo attachmentMDBRepo;
    final NoticesMDBRepo noticesMDBRepo;
    final RestHighLevelClient esClient;
    final MnisMembersRepo mnisMembersRepo;
    final ContractsFinderSvc contractsFinderSvc;

    public SearchRest(
            AttachmentMDBRepo attachmentMDBRepo,
            NoticesMDBRepo noticesMDBRepo,
            RestHighLevelClient esClient,
            MnisMembersRepo mnisMembersRepo,
            ContractsFinderSvc contractsFinderSvc) {
        this.attachmentMDBRepo = attachmentMDBRepo;
        this.noticesMDBRepo = noticesMDBRepo;
        this.esClient = esClient;
        this.mnisMembersRepo = mnisMembersRepo;
        this.contractsFinderSvc = contractsFinderSvc;
    }

    final ObjectMapper esSearchResponseObjectMapper = new ObjectMapper();

    @PostConstruct
    public void setup() {
        esSearchResponseObjectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        esSearchResponseObjectMapper.registerModule(new JavaTimeModule());
    }

    @PostMapping("/api/search/contract-finder")
    @JsonView(FosViews.Summary.class)
    public NoticeSearchResponse doContractsFinderSearch(
            @RequestBody ContractFinderSearchRequestDTO searchRequestDTO
    ) {
        SearchCriteriaType searchCriteria = new SearchCriteriaType().withKeyword(searchRequestDTO.getQuery()).withStatuses(
                new SearchCriteriaType.Statuses().withNoticeStatuses(NoticeStatusEnum.AWARDED)
        );
        if (searchRequestDTO.getDateType().equals(SearchByDateTypeEnum.awarded)) {
            searchCriteria.setAwardedFrom(searchRequestDTO.getDateRange().getDateFrom().atStartOfDay().atOffset(ZoneOffset.UTC));
            searchCriteria.setAwardedTo(OffsetDateTime.now());
        }
        else {
            searchCriteria.setPublishedFrom(searchRequestDTO.getDateRange().getDateFrom().atStartOfDay().atOffset(ZoneOffset.UTC));
            searchCriteria.setPublishedTo(OffsetDateTime.now());
        }
        return contractsFinderSvc.postSearchRequest(searchCriteria);
    }

    @PostMapping("/api/search/interests")
    public ESResponseWrapperDTO doSearchInterests(
            @RequestBody SearchRequestDTO searchRequestDTO
    ) throws Exception {
        SearchRequest request = new SearchRequest().indices("members_interests");
        SearchSourceBuilder sb = new SearchSourceBuilder();
        sb.query(
                QueryBuilders.matchQuery("text", null == searchRequestDTO.getQ() ? "" : searchRequestDTO.getQ())
                // .fuzziness(Fuzziness.AUTO)
        );
        sb.aggregation(AggregationBuilders.terms("personFullName").field("personFullName.keyword")
                .subAggregation(AggregationBuilders.topHits("top_hits").highlighter(new HighlightBuilder()
                        .preTags("<mark>")
                        .postTags("</mark>")
                        .field("text")
                ).size(5))
        );
        request.source(sb);
        SearchResponse response = esClient.search(request, RequestOptions.DEFAULT);
        ESResponseWrapperDTO wrapper = new ESResponseWrapperDTO(response);
        ((ParsedStringTerms) response.getAggregations().get("personFullName")).getBuckets().forEach(bucket -> {
            InterestSearchResponseDTO interestWrapper = new InterestSearchResponseDTO(bucket.getKeyAsString());
            for (Aggregation aggregation : bucket.getAggregations()) {
                if (aggregation instanceof ParsedTopHits) {
                    for (SearchHit hit : ((ParsedTopHits) aggregation).getHits()) {
                        try {
                            MemberInterest interest = esSearchResponseObjectMapper.readValue(hit.getSourceAsString(), MemberInterest.class);
                            InterestSearchResponseHitDTO searchResponseDTO = new InterestSearchResponseHitDTO(interest);
                            if (null != hit.getHighlightFields().get("text")) {
                                for (Text fragment : hit.getHighlightFields().get("text").getFragments()) {
                                    searchResponseDTO.getFragments().add(fragment.string());
                                }
                            }
                            interestWrapper.getTopHits().add(searchResponseDTO);
                            // save us doing a separate member lookup just to retrieve the ID
                            if (null == interestWrapper.getMnisPersonId()) {
                                interestWrapper.setMnisPersonId(interest.getMnisPersonId());
                            }
                        } catch (JsonProcessingException e) {
                            throw new FosEndpointException(e.getMessage(), e);
                        }
                    }
                }
            }
            wrapper.getResults().add(interestWrapper);
        });
        wrapper.setCount(wrapper.getResults().size());
        return wrapper;

    }

    @PostMapping("/api/search/attachments")
    public ESResponseWrapperDTO doSearch(
            @RequestBody SearchRequestDTO searchRequestDTO
    ) throws Exception {
        SearchRequest request = new SearchRequest().indices("attachments");
        SearchSourceBuilder sb = new SearchSourceBuilder();
        sb.query(
                QueryBuilders
                        .matchQuery("content", null == searchRequestDTO.getQ() ? "" : searchRequestDTO.getQ())
                        .fuzziness(Fuzziness.AUTO))
                .highlighter(new HighlightBuilder()
                        .preTags("<mark>")
                        .postTags("</mark>")
                        .field("content")
                );
        if (searchRequestDTO.getGroupResults()) {
            sb.aggregation(AggregationBuilders.terms("attachments").field(FosESFields.ATTACHMENT_ID));
        }
        request.source(sb);

        if (searchRequestDTO.getGroupResults()) {
            return aggregatedResults(esClient.search(request, RequestOptions.DEFAULT));
        } else {
            return singleResults(esClient.search(request, RequestOptions.DEFAULT));
        }
    }

    private ESResponseWrapperDTO aggregatedResults(SearchResponse response) {
        ESResponseWrapperDTO wrapper = new ESResponseWrapperDTO(response);
        ((ParsedStringTerms) response.getAggregations().get("attachments")).getBuckets().forEach(bucket -> {
            final Optional<Attachment> attachment = attachmentMDBRepo.findById(bucket.getKeyAsString());
            if (attachment.isEmpty()) {
                logger.trace("Unable to find attachment {}, skipping from search result", bucket.getKeyAsString());
                return;
            }

            final Optional<FullNotice> notice = noticesMDBRepo.findById(attachment.get().getNoticeId());
            if (notice.isEmpty()) {
                logger.trace("Unable to find notice {}, skipping from search result", bucket.getKeyAsString());
                return;
            }

            ESResult aggregationDTO = new ESAggregationDTO(attachment.get(), notice.get());
            aggregationDTO.setHits(bucket.getDocCount());

            // only add max of 3 results from each document / 'bucket'
            Arrays.stream(response.getHits().getHits())
                    .filter(hit -> hit.getSourceAsMap().get(FosESFields.ATTACHMENT_ID).equals(attachment.get().getId())).limit(3)
                    .forEach(page -> {
                        if (null != page.getHighlightFields().get("content")) {
                            Arrays.stream(page.getHighlightFields().get("content").getFragments())
                                    .forEach(content -> aggregationDTO.getFragments().add(content.toString()));
                        }
                    });

            wrapper.getResults().add(aggregationDTO);
        });
        wrapper.setCount(wrapper.getResults().size());
        return wrapper;
    }

    private ESResponseWrapperDTO singleResults(SearchResponse response) {
        ESResponseWrapperDTO wrapper = new ESResponseWrapperDTO(response);
        Arrays.stream(response.getHits().getHits())
                .forEach((SearchHit hit) -> {
                    final String noticeId = (String) hit.getSourceAsMap().get(FosESFields.NOTICE_ID);
                    final Optional<FullNotice> notice = noticesMDBRepo.findById(noticeId);
                    if (notice.isEmpty()) {
                        logger.trace("Unable to find notice {}, skipping from search result", noticeId);
                        return;
                    }

                    wrapper.getResults().add(
                            new ESSingleResponseDTO(notice.get())
                                    .setPageNumber((Integer) hit.getSourceAsMap().get(FosESFields.PAGE_NUMBER))
                                    .setAttachmentId((String) hit.getSourceAsMap().get(FosESFields.ATTACHMENT_ID))
                    );
                });
        wrapper.setCount(wrapper.getResults().size());
        return wrapper;
    }

}
