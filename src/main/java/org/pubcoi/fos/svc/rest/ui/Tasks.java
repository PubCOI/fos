package org.pubcoi.fos.svc.rest.ui;

import org.pubcoi.fos.svc.exceptions.FosBadRequestException;
import org.pubcoi.fos.svc.exceptions.FosException;
import org.pubcoi.fos.svc.gdb.ClientsGraphRepo;
import org.pubcoi.fos.svc.mdb.FosUserRepo;
import org.pubcoi.fos.svc.mdb.TasksRepo;
import org.pubcoi.fos.svc.models.core.DRTaskType;
import org.pubcoi.fos.svc.models.core.FosUITasks;
import org.pubcoi.fos.svc.models.core.FosUser;
import org.pubcoi.fos.svc.models.core.RequestWithAuth;
import org.pubcoi.fos.svc.models.dao.*;
import org.pubcoi.fos.svc.models.neo.nodes.ClientNode;
import org.pubcoi.fos.svc.rest.UI;
import org.pubcoi.fos.svc.services.TransactionOrchestrationSvc;
import org.pubcoi.fos.svc.transactions.FosTransactionBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
public class Tasks {
    private static final Logger logger = LoggerFactory.getLogger(Tasks.class);

    final TasksRepo tasksRepo;
    final TransactionOrchestrationSvc transactionOrch;
    final FosUserRepo userRepo;
    final ClientsGraphRepo clientGRepo;

    public Tasks(TasksRepo tasksRepo, TransactionOrchestrationSvc transactionOrch, FosUserRepo userRepo, ClientsGraphRepo clientGRepo) {
        this.tasksRepo = tasksRepo;
        this.transactionOrch = transactionOrch;
        this.userRepo = userRepo;
        this.clientGRepo = clientGRepo;
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

    @PutMapping("/api/ui/tasks")
    public CreateTaskResponseDAO createTask(
            @RequestBody CreateTaskRequestDAO createTask,
            @RequestHeader("authToken") String authToken
    ) {
        UI.checkAuth(authToken);
        logger.debug("{}", createTask);
        return new CreateTaskResponseDAO().setTaskId(UUID.randomUUID().toString());
    }

    @GetMapping("/api/ui/tasks/{taskType}/{refId}")
    public ResolveClientDAO getTaskDetails(@PathVariable("taskType") String taskType, @PathVariable("refId") String refID) {
        return new ResolveClientDAO(clientGRepo.findByIdEquals(refID).orElseThrow(() -> new FosException("Unable to find entity")));
    }

    @PutMapping(value = "/api/ui/tasks/{taskType}", consumes = "application/json")
    public UpdateClientDAO updateClientDAO(
            @PathVariable FosUITasks taskType,
            @RequestBody RequestWithAuth req
    ) {
        FosUser user = userRepo.getByUid(UI.checkAuth(req.getAuthToken()).getUid());

        /* ******** IMPORTANT ***********
         * This may look a bit convoluted in the fact that we're constructing transactions
         * here to be managed by the TransactionOrchestrationSvc but there's a good reason
         * for that - by coupling loosely, and enforcing transactions to be built via the
         * FosTransactionBuilder, we make it easier for transactions to be 'replayed' in
         * future on other systems.
         */

        if (taskType == FosUITasks.mark_canonical_clientNode) {
            if (null == req.getTaskId() || null == req.getTarget()) {
                throw new FosBadRequestException("Task ID and target must not be null");
            }

            clientGRepo.findByIdEquals(req.getTarget()).ifPresent(clientNode -> {
                transactionOrch.exec(FosTransactionBuilder.markCanonicalNode(clientNode, user, null));
            });

            markTaskCompleted(req.getTaskId(), user);
            return new UpdateClientDAO().setResponse("Resolved task: marked node as canonical");
        }

        if (taskType == FosUITasks.link_clientNode_to_parentClientNode) {
            if (null == req.getSource() || null == req.getTarget() || null == req.getTaskId()) {
                throw new FosBadRequestException("Task ID, Source and Target must be populated");
            }

            ClientNode source = clientGRepo.findClientHydratingNotices(req.getSource()).orElseThrow();
            ClientNode target = clientGRepo.findClientHydratingNotices(req.getTarget()).orElseThrow();

            transactionOrch.exec(FosTransactionBuilder.linkSourceToParent(source, target, user, null));

            markTaskCompleted(req.getTaskId(), user);

            return new UpdateClientDAO().setResponse(String.format(
                    "Resolved task: linked %s to %s", req.getSource(), req.getTarget()
            ));
        }

        throw new FosBadRequestException("Unable to find request type");
    }

    private void markTaskCompleted(String taskId, FosUser user) {
        tasksRepo.save(tasksRepo.getById(taskId).setCompleted(true).setCompletedBy(user).setCompletedDT(OffsetDateTime.now()));
    }

}
