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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.opencorporates.schemas.OCCompanySchema;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.Fuzziness;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.pubcoi.cdm.cf.ArrayOfFullNotice;
import org.pubcoi.cdm.cf.FullNotice;
import org.pubcoi.cdm.cf.attachments.Attachment;
import org.pubcoi.cdm.fos.FosESFields;
import org.pubcoi.fos.svc.exceptions.FosBadRequestException;
import org.pubcoi.fos.svc.exceptions.FosException;
import org.pubcoi.fos.svc.exceptions.FosUnauthorisedException;
import org.pubcoi.fos.svc.models.core.FosUser;
import org.pubcoi.fos.svc.models.core.SearchRequestDAO;
import org.pubcoi.fos.svc.models.dao.AttachmentDAO;
import org.pubcoi.fos.svc.models.dao.AwardDAO;
import org.pubcoi.fos.svc.models.dao.SearchTypeEnum;
import org.pubcoi.fos.svc.models.dao.TransactionDAO;
import org.pubcoi.fos.svc.models.dao.es.ESAggregationDTO;
import org.pubcoi.fos.svc.models.dao.es.ESResponseWrapperDTO;
import org.pubcoi.fos.svc.models.dao.es.ESResult;
import org.pubcoi.fos.svc.models.dao.es.ESSingleResponseDTO;
import org.pubcoi.fos.svc.models.mdb.UserObjectFlag;
import org.pubcoi.fos.svc.models.neo.nodes.OrganisationNode;
import org.pubcoi.fos.svc.repos.gdb.ClientsGraphRepo;
import org.pubcoi.fos.svc.repos.gdb.OrganisationsGraphRepo;
import org.pubcoi.fos.svc.repos.mdb.*;
import org.pubcoi.fos.svc.services.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@RestController
public class UI {
    private static final Logger logger = LoggerFactory.getLogger(UI.class);

    final AttachmentMDBRepo attachmentMDBRepo;
    final NoticesMDBRepo noticesMDBRepo;
    final NoticesSvc noticesSvc;
    final AwardsMDBRepo awardsMDBRepo;
    final ClientsGraphRepo clientGRepo;
    final FosUserRepo userRepo;
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

    public UI(
            AttachmentMDBRepo attachmentMDBRepo,
            NoticesMDBRepo noticesMDBRepo,
            NoticesSvc noticesSvc,
            AwardsMDBRepo awardsMDBRepo,
            ClientsGraphRepo clientGRepo,
            FosUserRepo userRepo,
            TransactionOrchestrationSvc transactionOrch,
            RestHighLevelClient esClient,
            S3Services s3Services,
            AwardsSvc awardsSvc,
            ScheduledSvc scheduledSvc,
            GraphSvc graphSvc,
            ApplicationStatusBean applicationStatus,
            UserObjectFlagRepo userObjectFlagRepo,
            OCCompaniesRepo ocCompaniesRepo,
            OrganisationsGraphRepo organisationsGraphRepo) {
        this.attachmentMDBRepo = attachmentMDBRepo;
        this.noticesMDBRepo = noticesMDBRepo;
        this.noticesSvc = noticesSvc;
        this.awardsMDBRepo = awardsMDBRepo;
        this.clientGRepo = clientGRepo;
        this.userRepo = userRepo;
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
    }

    @GetMapping("/api/awards")
    public List<AwardDAO> getContractAwards() {
        return awardsMDBRepo.findAll().stream().map(AwardDAO::new).collect(Collectors.toList());
    }

    @GetMapping("/api/transactions")
    public List<TransactionDAO> getTransactions() {
        return transactionOrch.getTransactions();
    }

    @PutMapping("/api/transactions")
    public String playbackTransactions(@RequestBody List<TransactionDAO> transactions) {
        AtomicBoolean hasErrors = new AtomicBoolean(false);
        AtomicInteger numErrors = new AtomicInteger(0);
        transactions.stream()
                .sorted(Comparator.comparing(TransactionDAO::getTransactionDT))
                .forEachOrdered(transaction -> {
                    boolean success = transactionOrch.exec(transaction);
                    if (!success) {
                        hasErrors.set(true);
                        numErrors.getAndIncrement();
                    }
                });
        return String.format("Transaction playback complete with %d errors", numErrors.get());
    }

    @PostMapping("/api/contracts")
    public String uploadContracts(MultipartHttpServletRequest request) {
        String uid = checkAuth(request.getParameter("authToken")).getUid();
        MultipartFile file = request.getFile("file");
        if (null == file) {
            throw new FosBadRequestException("Empty file");
        }
        try {
            JAXBContext context = JAXBContext.newInstance(ArrayOfFullNotice.class);
            Unmarshaller u = context.createUnmarshaller();
            ArrayOfFullNotice array = (ArrayOfFullNotice) u.unmarshal(new ByteArrayInputStream(file.getBytes()));
            for (FullNotice notice : array.getFullNotice()) {
                noticesSvc.addNotice(notice, uid);
            }
            scheduledSvc.populateFosOrgsMDBFromAwards();
            graphSvc.populateGraphFromMDB();
        } catch (IOException | JAXBException e) {
            throw new FosException("Unable to read file stream");
        }
        return "ok";
    }

    @GetMapping("/api/attachments/{attachmentId}/view")
    public ResponseEntity<String> viewRedirect(
            @PathVariable String attachmentId
    ) {
        Attachment attachment = attachmentMDBRepo.findById(attachmentId).orElseThrow(() -> new FosBadRequestException("Unable to find attachment"));
        // todo - check that we've got the location on the object ... for now just return where we think the doc should be
        // attachment.getS3Locations()
        try {
            return ResponseEntity
                    .status(HttpStatus.TEMPORARY_REDIRECT)
                    .location(s3Services.getSignedURL(attachment).toURI())
                    .build();
        } catch (URISyntaxException e) {
            throw new FosBadRequestException("Unable to get URL");
        }
    }

    @GetMapping("/api/attachments/{attachmentId}/metadata")
    public AttachmentDAO getAttachmentMetadata(
            @PathVariable String attachmentId
    ) {
        Attachment attachment = attachmentMDBRepo.findById(attachmentId).orElseThrow(() -> new FosBadRequestException("Unable to find attachment"));
        return new AttachmentDAO(attachment);
    }

    @PostMapping("/api/search")
    public ESResponseWrapperDTO doSearch(
            @RequestBody SearchRequestDAO searchRequestDAO,
            @RequestParam("type") SearchTypeEnum searchType
    ) throws Exception {
        SearchRequest request = new SearchRequest().indices("attachments");
        SearchSourceBuilder sb = new SearchSourceBuilder();
        sb.query(
                QueryBuilders
                        .matchQuery("content", null == searchRequestDAO.getQ() ? "" : searchRequestDAO.getQ())
                        .fuzziness(Fuzziness.AUTO))
                .highlighter(new HighlightBuilder()
                        .preTags("<mark>")
                        .postTags("</mark>")
                        .field("content")
                );
        if (searchRequestDAO.getGroupResults()) {
            sb.aggregation(AggregationBuilders.terms("attachments").field(FosESFields.ATTACHMENT_ID));
        }
        request.source(sb);

        if (searchRequestDAO.getGroupResults()) {
            return aggregatedResults(esClient.search(request, RequestOptions.DEFAULT));
        } else {
            return singleResults(esClient.search(request, RequestOptions.DEFAULT));
        }
    }

    private ESResponseWrapperDTO aggregatedResults(SearchResponse response) {
        ESResponseWrapperDTO wrapper = new ESResponseWrapperDTO(response);
        wrapper.setCount(response.getAggregations().asList().size());

        ((ParsedStringTerms) response.getAggregations().get("attachments")).getBuckets().forEach(bucket -> {
            final Attachment attachment = attachmentMDBRepo.findById(bucket.getKeyAsString()).orElseThrow(() -> new FosBadRequestException("Attachment not found"));
            final FullNotice notice = noticesMDBRepo.findById(attachment.getNoticeId()).orElseThrow(() -> new FosBadRequestException(String.format("Notice %s not found", attachment.getNoticeId())));

            ESResult aggregationDTO = new ESAggregationDTO(attachment, notice);
            aggregationDTO.setHits(bucket.getDocCount());

            // only add max of 3 results from each document / 'bucket'
            Arrays.stream(response.getHits().getHits())
                    .filter(hit -> hit.getSourceAsMap().get(FosESFields.ATTACHMENT_ID).equals(attachment.getId())).limit(3)
                    .forEach(page -> {
                        if (null != page.getHighlightFields().get("content")) {
                            Arrays.stream(page.getHighlightFields().get("content").getFragments())
                                    .forEach(content -> aggregationDTO.getFragments().add(content.toString()));
                        }
                    });

            wrapper.getResults().add(aggregationDTO);
        });
        return wrapper;
    }

    private ESResponseWrapperDTO singleResults(SearchResponse response) {
        ESResponseWrapperDTO wrapper = new ESResponseWrapperDTO(response);
        wrapper.setCount(response.getHits().getHits().length);

        Arrays.stream(response.getHits().getHits())
                .forEach((SearchHit hit) -> {
                    final String noticeId = (String) hit.getSourceAsMap().get(FosESFields.NOTICE_ID);
                    final FullNotice notice = noticesMDBRepo.findById(noticeId).orElseThrow(() -> new FosBadRequestException(String.format("Notice %s not found", noticeId)));
                    wrapper.getResults().add(
                            new ESSingleResponseDTO(notice)
                                    .setPageNumber((Integer) hit.getSourceAsMap().get(FosESFields.PAGE_NUMBER))
                                    .setAttachmentId((String) hit.getSourceAsMap().get(FosESFields.ATTACHMENT_ID))
                    );
                });
        return wrapper;
    }

    @GetMapping("/api/status")
    public ApplicationStatusBean getApplicationStatus() {
        return applicationStatus;
    }

    public static FirebaseToken checkAuth(String authToken) {
        try {
            return FirebaseAuth.getInstance().verifyIdToken(authToken);
        } catch (FirebaseAuthException e) {
            throw new FosUnauthorisedException();
        }
    }

    @PutMapping("/api/ui/flags/{type}/{objectId}")
    public String updateFlag(
            @RequestHeader("authToken") String authToken,
            @PathVariable String objectId,
            @PathVariable String type
    ) {
        String uid = checkAuth(authToken).getUid();
        FosUser user = userRepo.getByUid(uid);
        userObjectFlagRepo.save(new UserObjectFlag(objectId, user));
        if (type.equals("organisation")) {
            // if item is not already in db, add it
            if (!ocCompaniesRepo.existsById(objectId)) {
                // todo - put into separate thread
                scheduledSvc.getCompany(objectId);
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
        String uid = checkAuth(authToken).getUid();
        FosUser user = userRepo.getByUid(uid);
        userObjectFlagRepo.delete(new UserObjectFlag(objectId, user));
        return String.format("Removed flag on item %s", objectId);
    }
}
