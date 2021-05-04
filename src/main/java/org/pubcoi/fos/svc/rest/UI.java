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
import com.opencorporates.schemas.OCCompanySchema;
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
import org.pubcoi.fos.svc.exceptions.FosBadRequestResponseStatusException;
import org.pubcoi.fos.svc.exceptions.FosRecordNotFoundException;
import org.pubcoi.fos.svc.exceptions.FosResponseStatusException;
import org.pubcoi.fos.svc.models.core.CFAward;
import org.pubcoi.fos.svc.models.core.FosUser;
import org.pubcoi.fos.svc.models.core.SearchRequestDTO;
import org.pubcoi.fos.svc.models.dto.AttachmentDTO;
import org.pubcoi.fos.svc.models.dto.TransactionDTO;
import org.pubcoi.fos.svc.models.dto.es.*;
import org.pubcoi.fos.svc.models.dto.search.ContractFinderSearchRequestDTO;
import org.pubcoi.fos.svc.models.dto.search.SearchByDateTypeEnum;
import org.pubcoi.fos.svc.models.es.MemberInterest;
import org.pubcoi.fos.svc.models.mdb.UserObjectFlag;
import org.pubcoi.fos.svc.models.neo.nodes.OrganisationNode;
import org.pubcoi.fos.svc.models.queries.AwardsListResponseDTO;
import org.pubcoi.fos.svc.repos.gdb.custom.AwardsListRepo;
import org.pubcoi.fos.svc.repos.gdb.jpa.ClientsGraphRepo;
import org.pubcoi.fos.svc.repos.gdb.jpa.OrganisationsGraphRepo;
import org.pubcoi.fos.svc.repos.mdb.*;
import org.pubcoi.fos.svc.services.*;
import org.pubcoi.fos.svc.services.auth.FosAuthProvider;
import org.pubcoi.fos.views.FosViews;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.net.URISyntaxException;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
public class UI {
    private static final Logger logger = LoggerFactory.getLogger(UI.class);

    final FosAuthProvider authProvider;
    final AttachmentMDBRepo attachmentMDBRepo;
    final NoticesMDBRepo noticesMDBRepo;
    final NoticesSvc noticesSvc;
    final AwardsMDBRepo awardsMDBRepo;
    final ClientsGraphRepo clientGRepo;
    final TransactionOrchestrationSvc transactionOrch;
    final RestHighLevelClient esClient;
    final S3Services s3Services;
    final AwardsSvc awardsSvc;
    final ScheduledSvc scheduledSvc;
    final GraphSvc graphSvc;
    final ApplicationStatusBean applicationStatus;
    final UserObjectFlagRepo userObjectFlagRepo;
    final OCCompaniesRepo ocCompaniesRepo;
    final OrganisationsGraphRepo organisationsGraphRepo;
    final MnisMembersRepo mnisMembersRepo;
    final MnisSvc mnisSvc;
    final AwardsListRepo awardsListRepo;
    final ContractsFinderSvc contractsFinderSvc;

    public UI(
            FosAuthProvider authProvider,
            AttachmentMDBRepo attachmentMDBRepo,
            NoticesMDBRepo noticesMDBRepo,
            NoticesSvc noticesSvc,
            AwardsMDBRepo awardsMDBRepo,
            ClientsGraphRepo clientGRepo,
            TransactionOrchestrationSvc transactionOrch,
            RestHighLevelClient esClient,
            S3Services s3Services,
            AwardsSvc awardsSvc,
            ScheduledSvc scheduledSvc,
            GraphSvc graphSvc,
            ApplicationStatusBean applicationStatus,
            UserObjectFlagRepo userObjectFlagRepo,
            OCCompaniesRepo ocCompaniesRepo,
            OrganisationsGraphRepo organisationsGraphRepo,
            MnisMembersRepo mnisMembersRepo,
            MnisSvc mnisSvc,
            AwardsListRepo awardsListRepo,
            ContractsFinderSvc contractsFinderSvc) {
        this.authProvider = authProvider;
        this.attachmentMDBRepo = attachmentMDBRepo;
        this.noticesMDBRepo = noticesMDBRepo;
        this.noticesSvc = noticesSvc;
        this.awardsMDBRepo = awardsMDBRepo;
        this.clientGRepo = clientGRepo;
        this.transactionOrch = transactionOrch;
        this.esClient = esClient;
        this.s3Services = s3Services;
        this.awardsSvc = awardsSvc;
        this.scheduledSvc = scheduledSvc;
        this.graphSvc = graphSvc;
        this.applicationStatus = applicationStatus;
        this.userObjectFlagRepo = userObjectFlagRepo;
        this.ocCompaniesRepo = ocCompaniesRepo;
        this.organisationsGraphRepo = organisationsGraphRepo;
        this.mnisMembersRepo = mnisMembersRepo;
        this.mnisSvc = mnisSvc;
        this.awardsListRepo = awardsListRepo;
        this.contractsFinderSvc = contractsFinderSvc;
    }

    final ObjectMapper esSearchResponseObjectMapper = new ObjectMapper();

    @PostConstruct
    public void setup() {
        esSearchResponseObjectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        esSearchResponseObjectMapper.registerModule(new JavaTimeModule());
    }

    @GetMapping("/api/awards")
    public List<AwardsListResponseDTO> getContractAwards() {
        return awardsListRepo.getAwardsWithRels()
                .stream().map(AwardsListResponseDTO::new)
                .filter(a -> awardsMDBRepo.existsById(a.getId())) // todo show info stats for awards that are missing
                .peek(a -> {
                    CFAward award = awardsMDBRepo.findById(a.getId()).orElseThrow();
                    a.setValueMin(award.getValueMin());
                    a.setValueMax(award.getValueMax());
                    a.setNoticeId(award.getNoticeId());
                })
                .collect(Collectors.toList());
    }

    @GetMapping("/api/transactions")
    public List<TransactionDTO> getTransactions() {
        return transactionOrch.getTransactions();
    }

    @GetMapping("/api/attachments/{attachmentId}/view")
    public ResponseEntity<String> viewRedirect(
            @PathVariable String attachmentId
    ) {
        Attachment attachment = attachmentMDBRepo.findById(attachmentId).orElseThrow(() -> new FosBadRequestResponseStatusException("Unable to find attachment"));
        // todo - check that we've got the location on the object ... for now just return where we think the doc should be
        // attachment.getS3Locations()
        try {
            return ResponseEntity
                    .status(HttpStatus.TEMPORARY_REDIRECT)
                    .location(s3Services.getSignedURL(attachment).toURI())
                    .build();
        } catch (URISyntaxException e) {
            throw new FosBadRequestResponseStatusException("Unable to get URL");
        }
    }

    @GetMapping("/api/attachments/{attachmentId}/metadata")
    public AttachmentDTO getAttachmentMetadata(
            @PathVariable String attachmentId
    ) {
        Attachment attachment = attachmentMDBRepo.findById(attachmentId).orElseThrow(() -> new FosBadRequestResponseStatusException("Unable to find attachment"));
        return new AttachmentDTO(attachment);
    }


    @GetMapping("/api/interests/{mnisMemberId}")
    public MemberInterestsDTO getInterests(@PathVariable Integer mnisMemberId) {
        return mnisSvc.getInterestsDTOForMember(mnisMemberId);
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
            searchCriteria.setAwardedTo(searchRequestDTO.getDateRange().getDateFrom().atStartOfDay().atOffset(ZoneOffset.UTC));
        }
        else {
            searchCriteria.setPublishedFrom(searchRequestDTO.getDateRange().getDateFrom().atStartOfDay().atOffset(ZoneOffset.UTC));
            searchCriteria.setPublishedTo(searchRequestDTO.getDateRange().getDateFrom().atStartOfDay().atOffset(ZoneOffset.UTC));
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
                            throw new FosResponseStatusException(e.getMessage(), e);
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

    @GetMapping("/api/status")
    public ApplicationStatusBean getApplicationStatus() {
        return applicationStatus;
    }

    @PutMapping("/api/ui/flags/{type}/{objectId}")
    public String updateFlag(
            @RequestHeader("authToken") String authToken,
            @PathVariable String objectId,
            @PathVariable String type
    ) {
        String uid = authProvider.getUid(authToken);
        FosUser user = authProvider.getByUid(uid);
        userObjectFlagRepo.save(new UserObjectFlag(objectId, user));
        if (type.equals("organisation")) {
            // if item is not already in db, add it
            if (!ocCompaniesRepo.existsById(objectId)) {
                // todo - put into separate thread
                try {
                    scheduledSvc.getCompany(objectId);
                } catch (FosRecordNotFoundException e) {
                    logger.info(String.format("Unable to find company record: %s", e.getMessage()));
                }
            }
            // should now be in MDB repo, check if it's in graph
            if (ocCompaniesRepo.existsById(objectId)) {
                OCCompanySchema companySchema = ocCompaniesRepo.findById(objectId).orElseThrow();
                if (!organisationsGraphRepo.existsByJurisdictionAndReference(companySchema.getJurisdictionCode(), companySchema.getCompanyNumber())) {
                    OrganisationNode node = organisationsGraphRepo.save(new OrganisationNode(companySchema));
                    logger.info("Created new org {} in graph", node);
                }
            }
        }
        return String.format("Flagged item %s", objectId);
    }

    @DeleteMapping("/api/ui/flags/{type}/{objectId}")
    public String deleteFlag(
            @RequestHeader("authToken") String authToken,
            @PathVariable String objectId,
            @PathVariable String type
    ) {
        String uid = authProvider.getUid(authToken);
        FosUser user = authProvider.getByUid(uid);
        userObjectFlagRepo.delete(new UserObjectFlag(objectId, user));
        return String.format("Removed flag on item %s", objectId);
    }
}
