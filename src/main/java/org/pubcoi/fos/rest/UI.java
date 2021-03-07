package org.pubcoi.fos.rest;

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
import org.pubcoi.fos.cdm.FosESFields;
import org.pubcoi.fos.cdm.attachments.Attachment;
import org.pubcoi.fos.exceptions.FOSBadRequestException;
import org.pubcoi.fos.exceptions.FOSException;
import org.pubcoi.fos.exceptions.FOSUnauthorisedException;
import org.pubcoi.fos.gdb.ClientNodeFTS;
import org.pubcoi.fos.gdb.ClientsGraphRepo;
import org.pubcoi.fos.mdb.*;
import org.pubcoi.fos.models.cf.ArrayOfFullNotice;
import org.pubcoi.fos.models.cf.FullNotice;
import org.pubcoi.fos.models.core.*;
import org.pubcoi.fos.models.core.transactions.CanonicaliseClientNode;
import org.pubcoi.fos.models.core.transactions.LinkSourceToParentClient;
import org.pubcoi.fos.models.dao.*;
import org.pubcoi.fos.models.neo.nodes.ClientNode;
import org.pubcoi.fos.services.S3Services;
import org.pubcoi.fos.services.TransactionSvc;
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
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@RestController
public class UI {
    private static final Logger logger = LoggerFactory.getLogger(UI.class);

    final AttachmentMDBRepo attachmentMDBRepo;
    final NoticesMDBRepo noticesMDBRepo;
    final AwardsMDBRepo awardsMDBRepo;
    final TasksRepo tasksRepo;
    final ClientsGraphRepo clientGRepo;
    final FOSUserRepo userRepo;
    final TransactionSvc transactionSvc;
    final ClientNodeFTS clientNodeFTS;
    final RestHighLevelClient esClient;
    final S3Services s3Services;

    public UI(AttachmentMDBRepo attachmentMDBRepo,
              NoticesMDBRepo noticesMDBRepo,
              AwardsMDBRepo awardsMDBRepo,
              TasksRepo tasksRepo,
              ClientsGraphRepo clientGRepo,
              FOSUserRepo userRepo,
              TransactionSvc transactionSvc,
              ClientNodeFTS clientNodeFTS,
              RestHighLevelClient esClient, S3Services s3Services) {
        this.attachmentMDBRepo = attachmentMDBRepo;
        this.noticesMDBRepo = noticesMDBRepo;
        this.awardsMDBRepo = awardsMDBRepo;
        this.tasksRepo = tasksRepo;
        this.clientGRepo = clientGRepo;
        this.userRepo = userRepo;
        this.transactionSvc = transactionSvc;
        this.clientNodeFTS = clientNodeFTS;
        this.esClient = esClient;
        this.s3Services = s3Services;
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
            userRepo.save(new FOSUser()
                    .setUid(loginDAO.getUid())
                    .setDisplayName(record.getDisplayName())
                    .setLastLogin(OffsetDateTime.now())
            );
        } catch (FirebaseAuthException e) {
            logger.error(e.getMessage(), e);
            throw new FOSException();
        }
    }

    @GetMapping("/api/ui/awards")
    public List<AwardDAO> getContractAwards() {
        return awardsMDBRepo.findAll().stream().map(AwardDAO::new).collect(Collectors.toList());
    }

    @PostMapping("/api/ui/user")
    public UserProfileDAO getUserProfile(@RequestBody RequestWithAuth auth) {
        String uid = checkAuth(auth.getAuthToken()).getUid();
        return new UserProfileDAO(userRepo.getByUid(uid));
    }

    @GetMapping("/api/ui/tasks")
    public List<TaskDAO> getTasks(@RequestParam(value = "completed", defaultValue = "false") Boolean completed) {
        return tasksRepo.findAll().stream()
                .filter(t -> t.getCompleted().equals(completed))
                .map(TaskDAO::new)
                .peek(t -> {
                    if (t.getTaskType().equals(DRTaskType.resolve_client)) {
                        Optional<ClientNode> clientNode = clientGRepo.findByIdEquals(t.getEntity());
                        if (!clientNode.isPresent()) {
                            logger.error("Unable to find ClientNode {}", t.getEntity());
                        } else {
                            t.setDescription(String.format(
                                    "Verify details for entity: %s", clientNode.get().getName())
                            );
                        }
                    }
                })
                .collect(Collectors.toList());
    }

    @GetMapping("/api/ui/tasks/{taskType}/{refID}")
    public ResolveClientDAO getTaskDetails(@PathVariable("taskType") String taskType, @PathVariable("refID") String refID) {
        return new ResolveClientDAO(clientGRepo.findByIdEquals(refID).orElseThrow(() -> new FOSException("Unable to find entity")));
    }

    @PutMapping(value = "/api/ui/tasks/{taskType}", consumes = "application/json")
    public UpdateClientDAO updateClientDAO(
            @PathVariable FOSUITasks taskType,
            @RequestBody RequestWithAuth req
    ) {
        FOSUser user = userRepo.getByUid(checkAuth(req.getAuthToken()).getUid());
        switch (taskType) {
            case mark_canonical_clientNode:
                if (null == req.getTaskID() || null == req.getTarget()) {
                    throw new FOSBadRequestException("Task ID and target must not be null");
                }
                logger.debug("{}: target:{}", taskType, req.getTarget());
                clientGRepo.findByIdEquals(req.getTarget()).ifPresent(clientNode -> {
                    transactionSvc.doTransaction(CanonicaliseClientNode.build(clientNode, user, null));
                });
                logger.debug("{}: target:{} - marking {} as COMPLETE", taskType, req.getTarget(), req.getTaskID());
                markTaskCompleted(req.getTaskID(), user);
                return new UpdateClientDAO().setResponse("Resolved task: marked node as canonical");
            case link_clientNode_to_parentClientNode:
                if (null == req.getSource() || null == req.getTarget() || null == req.getTaskID()) {
                    throw new FOSBadRequestException("Task ID, Source and Target must be populated");
                }
                logger.debug("{}: source:{} target:{}", taskType, req.getSource(), req.getTarget());

                Optional<ClientNode> source = clientGRepo.findByIdEquals(req.getSource());
                Optional<ClientNode> target = clientGRepo.findByIdEquals(req.getTarget());
                if (!source.isPresent() || !target.isPresent()) {
                    throw new FOSBadRequestException("Unable to resolve ClientNodes");
                }
                transactionSvc.doTransaction(LinkSourceToParentClient.build(source.get(), target.get(), user));

                logger.debug("{}: source:{} target:{} - marking {} as COMPLETE", taskType, req.getSource(), req.getTarget(), req.getTaskID());
                markTaskCompleted(req.getTaskID(), user);

                return new UpdateClientDAO().setResponse(String.format("Resolved task: linked %s to %s", req.getSource(), req.getTarget()));
            default:
                logger.warn("Did not match action to request");
        }
        return new UpdateClientDAO().setResponse("updated");
    }

    private void markTaskCompleted(String taskID, FOSUser user) {
        tasksRepo.save(tasksRepo.getById(taskID).setCompleted(true).setCompletedBy(user).setCompletedDT(OffsetDateTime.now()));
    }

    /**
     * Return canonical client nodes that match the current client name (if any)
     *
     * @param query The search parameters
     * @return List of top responses, ordered by best -> worst match
     */
    @GetMapping("/api/ui/graphs/clients")
    public List<ClientNodeFTSDAOResponse> runQuery(@RequestParam String query, @RequestParam(required = false) String currentNode) {
        if (query.isEmpty()) return new ArrayList<>();
        return clientNodeFTS.findAllDTOProjectionsWithCustomQuery(query)
                .stream()
                .filter(c -> !c.getId().equals(currentNode))
                .limit(5)
                .map(ClientNodeFTSDAOResponse::new)
                .collect(Collectors.toList());
    }

    private FirebaseToken checkAuth(String authToken) {
        try {
            return FirebaseAuth.getInstance().verifyIdToken(authToken);
        } catch (FirebaseAuthException e) {
            throw new FOSUnauthorisedException();
        }
    }

    @GetMapping("/api/transactions")
    public List<TransactionDAO> getTransactions() {
        return transactionSvc.getTransactions();
    }

    @PutMapping("/api/transactions")
    public String playbackTransactions(@RequestBody List<TransactionDAO> transactions) {
        AtomicBoolean hasErrors = new AtomicBoolean(false);
        AtomicInteger numErrors = new AtomicInteger(0);
        transactions.stream()
                .sorted(Comparator.comparing(TransactionDAO::getTransactionDT))
                .forEachOrdered(transaction -> {
                    boolean success = transactionSvc.doTransaction(transaction);
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
            throw new FOSBadRequestException("Empty file");
        }
        try {
            JAXBContext context = JAXBContext.newInstance(ArrayOfFullNotice.class);
            Unmarshaller u = context.createUnmarshaller();
            ArrayOfFullNotice array = (ArrayOfFullNotice) u.unmarshal(new ByteArrayInputStream(file.getBytes()));
            for (FullNotice notice : array.getFullNotice()) {
                logger.debug("got notice: " + notice.getId());
            }
        } catch (IOException | JAXBException e) {
            throw new FOSException("Unable to read file stream");
        }
        return "ok";
    }

    @GetMapping("/api/ui/view")
    public ResponseEntity<String> viewRedirect(
            @RequestParam("attachment_id") String attachmentId
    ) {
        Attachment attachment = attachmentMDBRepo.findById(attachmentId).orElseThrow(() -> new FOSBadRequestException("Unable to find attachment"));
        // todo - check that we've got the location on the object ... for now just return where we think the doc should be
        // attachment.getS3Locations()
        try {
            return ResponseEntity
                    .status(HttpStatus.TEMPORARY_REDIRECT)
                    .location(s3Services.getSignedURL(attachment).toURI())
                    .build();
        } catch (URISyntaxException e) {
            throw new FOSBadRequestException("Unable to get URL");
        }
    }

    @PostMapping("/api/ui/search")
    public ESResponseWrapperDTO doSearch(
            @RequestBody SearchRequestDAO searchRequestDAO
    ) throws Exception {
        SearchRequest request = new SearchRequest();
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
            final Attachment attachment = attachmentMDBRepo.findById(bucket.getKeyAsString()).orElseThrow(() -> new FOSBadRequestException("Attachment not found"));
            final FullNotice notice = noticesMDBRepo.findById(attachment.getNoticeId()).orElseThrow(() -> new FOSBadRequestException(String.format("Notice %s not found", attachment.getNoticeId())));

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
                    final FullNotice notice = noticesMDBRepo.findById(noticeId).orElseThrow(() -> new FOSBadRequestException(String.format("Notice %s not found", noticeId)));
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
}
