package org.pubcoi.fos.rest;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.google.firebase.auth.UserRecord;
import org.pubcoi.fos.exceptions.FOSException;
import org.pubcoi.fos.exceptions.FOSRuntimeException;
import org.pubcoi.fos.exceptions.FOSUnauthorisedException;
import org.pubcoi.fos.gdb.ClientsGraphRepo;
import org.pubcoi.fos.mdb.AwardsMDBRepo;
import org.pubcoi.fos.mdb.FOSUserRepo;
import org.pubcoi.fos.mdb.NoticesMDBRepo;
import org.pubcoi.fos.mdb.TasksRepo;
import org.pubcoi.fos.models.core.DRTaskType;
import org.pubcoi.fos.models.core.FOSUser;
import org.pubcoi.fos.models.core.RequestWithAuth;
import org.pubcoi.fos.models.dao.*;
import org.pubcoi.fos.models.neo.nodes.ClientNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class UI {
    private static final Logger logger = LoggerFactory.getLogger(UI.class);

    NoticesMDBRepo noticesMDBRepo;
    AwardsMDBRepo awardsMDBRepo;
    TasksRepo tasksRepo;
    ClientsGraphRepo clientGRepo;
    FOSUserRepo userRepo;

    public UI(NoticesMDBRepo noticesMDBRepo, AwardsMDBRepo awardsMDBRepo, TasksRepo tasksRepo, ClientsGraphRepo clientGRepo, FOSUserRepo userRepo) {
        this.noticesMDBRepo = noticesMDBRepo;
        this.awardsMDBRepo = awardsMDBRepo;
        this.tasksRepo = tasksRepo;
        this.clientGRepo = clientGRepo;
        this.userRepo = userRepo;
    }

    @PostMapping("/api/ui/login")
    public void doLogin(@RequestBody UserLoginDAO loginDAO) throws FOSException {
        // shortcut .. if we already have the UID, we know we've created the user:
        // if no match, it could be because the UID refers to another provider (ie they initially
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
    public UserProfileDAO getUserProfile(@RequestBody RequestWithAuth auth) throws FOSUnauthorisedException {
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
                        ClientNode clientNode = clientGRepo.findById(t.getEntity()).orElseThrow(() -> new FOSRuntimeException("Unable to find client"));
                        t.setDescription(String.format(
                                "Verify details for entity: %s", clientNode.getClientName())
                        );
                    }
                })
                .collect(Collectors.toList());
    }

    @GetMapping("/api/ui/tasks/{taskType}/{refID}")
    public ResolveClientDAO getTaskDetails(@PathVariable("taskType") String taskType, @PathVariable("refID") String refID) throws FOSException {
        return new ResolveClientDAO(clientGRepo.findById(refID).orElseThrow(() -> new FOSException("Unable to find entity")));
    }

    @PutMapping(value = "/api/ui/tasks/{taskType}/{refID}", consumes = "application/json")
    public UpdateClientDAO updateClientDAO(
            @RequestParam("canonical") Boolean canonical,
            @PathVariable String refID,
            @PathVariable String taskType,
            @RequestBody RequestWithAuth authToken
    ) throws FOSUnauthorisedException {
        checkAuth(authToken.getAuthToken());
        return new UpdateClientDAO().setResponse("updated");
    }

    private FirebaseToken checkAuth(String authToken) throws FOSUnauthorisedException {
        try {
            return FirebaseAuth.getInstance().verifyIdToken(authToken);
        } catch (FirebaseAuthException e) {
            throw new FOSUnauthorisedException();
        }
    }
}
