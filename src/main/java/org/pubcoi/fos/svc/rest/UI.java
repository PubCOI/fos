package org.pubcoi.fos.svc.rest;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.google.firebase.auth.UserRecord;
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
import org.pubcoi.fos.svc.gdb.ClientsGraphRepo;
import org.pubcoi.fos.svc.mdb.AttachmentMDBRepo;
import org.pubcoi.fos.svc.mdb.AwardsMDBRepo;
import org.pubcoi.fos.svc.mdb.FosUserRepo;
import org.pubcoi.fos.svc.mdb.NoticesMDBRepo;
import org.pubcoi.fos.svc.models.core.FosUser;
import org.pubcoi.fos.svc.models.core.SearchRequestDAO;
import org.pubcoi.fos.svc.models.dao.*;
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
import java.time.OffsetDateTime;
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

    public UI(
            AttachmentMDBRepo attachmentMDBRepo,
            NoticesMDBRepo noticesMDBRepo,
            NoticesSvc noticesSvc, AwardsMDBRepo awardsMDBRepo,
            ClientsGraphRepo clientGRepo,
            FosUserRepo userRepo,
            TransactionOrchestrationSvc transactionOrch,
            RestHighLevelClient esClient,
            S3Services s3Services,
            AwardsSvc awardsSvc,
            ScheduledSvc scheduledSvc, GraphSvc graphSvc, ApplicationStatusBean applicationStatus) {
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
    }

    @PostMapping("/api/ui/login")
    public void doLogin(@RequestBody UserLoginDAO loginDAO) {
        // shortcut .. if we already have the UID, we know we've created the user:
        // if no match, it could be because the UID refers to another provider (eg they initially
        // logged in via GitHub but are now using Google
        if (userRepo.existsByUid(loginDAO.getUid())) {
            userRepo.save(userRepo.getByUid(loginDAO.getUid()).setLastLogin(OffsetDateTime.now()));
            return;
        }

        // adds user meta if it doesn't exist
        try {
            UserRecord record = FirebaseAuth.getInstance().getUser(loginDAO.getUid());
            userRepo.save(new FosUser()
                    .setUid(loginDAO.getUid())
                    .setDisplayName(record.getDisplayName())
                    .setLastLogin(OffsetDateTime.now())
            );
        } catch (FirebaseAuthException e) {
            logger.error(e.getMessage(), e);
            throw new FosException();
        }
    }

    @GetMapping("/api/ui/awards")
    public List<AwardDAO> getContractAwards() {
        return awardsMDBRepo.findAll().stream().map(AwardDAO::new).collect(Collectors.toList());
    }

    @GetMapping("/api/ui/awards/{awardId}")
    public AwardDAO getAward(@PathVariable String awardId) {
        return awardsSvc.getAwardDetailsDAOWithAttachments(awardId);
    }

    @PostMapping("/api/ui/user")
    public UserProfileDAO getUserProfile(
            @RequestHeader("authToken") String authToken
    ) {
        String uid = UI.checkAuth(authToken).getUid();
        return new UserProfileDAO(userRepo.getByUid(uid));
    }

    @PutMapping("/api/ui/user")
    public UserProfileDAO updateUserProfile(
            @RequestBody UpdateProfileRequestDAO updateProfileRequestDAO,
            @RequestHeader("authToken") String authToken
    ) {
        String uid = UI.checkAuth(authToken).getUid();
        FosUser user = userRepo.getByUid(uid);
        if (null == user) throw new FosBadRequestException("Unable to find user");
        return new UserProfileDAO(userRepo.save(user.setDisplayName(updateProfileRequestDAO.getDisplayName())));
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

    @PostMapping("/api/ui/data/contracts")
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

    @GetMapping("/api/ui/view")
    public ResponseEntity<String> viewRedirect(
            @RequestParam("attachment_id") String attachmentId
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

    @PostMapping("/api/ui/search")
    public ESResponseWrapperDTO doSearch(
            @RequestBody SearchRequestDAO searchRequestDAO
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
        wrapper.setResults(response.getAggregations().asList().size());

        ((ParsedStringTerms) response.getAggregations().get("attachments")).getBuckets().forEach(bucket -> {
            final Attachment attachment = attachmentMDBRepo.findById(bucket.getKeyAsString()).orElseThrow(() -> new FosBadRequestException("Attachment not found"));
            final FullNotice notice = noticesMDBRepo.findById(attachment.getNoticeId()).orElseThrow(() -> new FosBadRequestException(String.format("Notice %s not found", attachment.getNoticeId())));

            ESAggregationDTO aggregationDTO = new ESAggregationDTO()
                    .setKey(String.format("aggregation-%s", attachment.getId()))
                    .setAttachmentId(attachment.getId())
                    .setNoticeId(attachment.getNoticeId())
                    .setHits(bucket.getDocCount())
                    .setOrganisation(notice.getNotice().getOrganisationName())
                    .setNoticeDescription(notice.getNotice().getDescription())
                    .setNoticeDT(notice.getCreatedDate());

            // only add max of 3 results from each document / 'bucket'
            Arrays.stream(response.getHits().getHits())
                    .filter(hit -> hit.getSourceAsMap().get(FosESFields.ATTACHMENT_ID).equals(attachment.getId())).limit(3)
                    .forEach(page -> {
                        if (null != page.getHighlightFields().get("content")) {
                            Arrays.stream(page.getHighlightFields().get("content").getFragments())
                                    .forEach(content -> aggregationDTO.getFragments().add(content.toString()));
                        }
                    });

            wrapper.getAggregated().add(aggregationDTO);
        });
        return wrapper;
    }

    private ESResponseWrapperDTO singleResults(SearchResponse response) {
        ESResponseWrapperDTO wrapper = new ESResponseWrapperDTO(response);
        wrapper.setResults(response.getHits().getHits().length);

        Arrays.stream(response.getHits().getHits())
                .forEach((SearchHit hit) -> {
                    final String noticeId = (String) hit.getSourceAsMap().get(FosESFields.NOTICE_ID);
                    final FullNotice notice = noticesMDBRepo.findById(noticeId).orElseThrow(() -> new FosBadRequestException(String.format("Notice %s not found", noticeId)));
                    StringBuilder noticeInfo = new StringBuilder("Notice: ");
                    if (null != notice.getNotice().getDescription()) {
                        noticeInfo.append(notice.getNotice().getDescription());
                    }
                    wrapper.getPaged().add(
                            new ESResponseDTO(hit)
                                    .setPageNumber((Integer) hit.getSourceAsMap().get(FosESFields.PAGE_NUMBER))
                                    .setAttachmentId((String) hit.getSourceAsMap().get(FosESFields.ATTACHMENT_ID))
                                    .setNoticeId(noticeId)
                                    .setOrganisation(notice.getNotice().getOrganisationName())
                                    .setNoticeDescription(noticeInfo.toString())
                                    .setNoticeDT(notice.getCreatedDate())
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
}
