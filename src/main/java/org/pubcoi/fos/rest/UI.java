package org.pubcoi.fos.rest;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.google.firebase.auth.UserRecord;
import org.pubcoi.fos.exceptions.FOSBadRequestException;
import org.pubcoi.fos.exceptions.FOSException;
import org.pubcoi.fos.exceptions.FOSUnauthorisedException;
import org.pubcoi.fos.gdb.ClientNodeFTS;
import org.pubcoi.fos.gdb.ClientsGraphRepo;
import org.pubcoi.fos.mdb.AwardsMDBRepo;
import org.pubcoi.fos.mdb.FOSUserRepo;
import org.pubcoi.fos.mdb.NoticesMDBRepo;
import org.pubcoi.fos.mdb.TasksRepo;
import org.pubcoi.fos.models.cf.ArrayOfFullNotice;
import org.pubcoi.fos.models.cf.FullNotice;
import org.pubcoi.fos.models.core.DRTaskType;
import org.pubcoi.fos.models.core.FOSUITasks;
import org.pubcoi.fos.models.core.FOSUser;
import org.pubcoi.fos.models.core.RequestWithAuth;
import org.pubcoi.fos.models.core.transactions.CanonicaliseClientNode;
import org.pubcoi.fos.models.core.transactions.LinkSourceToParentClient;
import org.pubcoi.fos.models.dao.*;
import org.pubcoi.fos.models.neo.nodes.ClientNode;
import org.pubcoi.fos.services.TransactionSvc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@RestController
public class UI {
    private static final Logger logger = LoggerFactory.getLogger(UI.class);

    NoticesMDBRepo noticesMDBRepo;
    AwardsMDBRepo awardsMDBRepo;
    TasksRepo tasksRepo;
    ClientsGraphRepo clientGRepo;
    FOSUserRepo userRepo;
    TransactionSvc transactionSvc;
    ClientNodeFTS clientNodeFTS;

    public UI(NoticesMDBRepo noticesMDBRepo, AwardsMDBRepo awardsMDBRepo, TasksRepo tasksRepo, ClientsGraphRepo clientGRepo, FOSUserRepo userRepo, TransactionSvc transactionSvc, ClientNodeFTS clientNodeFTS) {
        this.noticesMDBRepo = noticesMDBRepo;
        this.awardsMDBRepo = awardsMDBRepo;
        this.tasksRepo = tasksRepo;
        this.clientGRepo = clientGRepo;
        this.userRepo = userRepo;
        this.transactionSvc = transactionSvc;
        this.clientNodeFTS = clientNodeFTS;
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
                                    "Verify details for entity: %s", clientNode.get().getClientName())
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
}
