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

package org.pubcoi.fos.svc.rest.ui;

import com.opencorporates.schemas.OCCompanySchema;
import org.pubcoi.fos.svc.exceptions.FosBadRequestException;
import org.pubcoi.fos.svc.exceptions.FosException;
import org.pubcoi.fos.svc.models.core.*;
import org.pubcoi.fos.svc.models.dao.*;
import org.pubcoi.fos.svc.models.dao.tasks.UpdateNodeDAO;
import org.pubcoi.fos.svc.models.neo.nodes.ClientNode;
import org.pubcoi.fos.svc.models.neo.nodes.OrganisationNode;
import org.pubcoi.fos.svc.models.oc.OCWrapper;
import org.pubcoi.fos.svc.repos.gdb.ClientsGraphRepo;
import org.pubcoi.fos.svc.repos.gdb.OrganisationsGraphRepo;
import org.pubcoi.fos.svc.repos.mdb.FosUserRepo;
import org.pubcoi.fos.svc.repos.mdb.OrganisationsMDBRepo;
import org.pubcoi.fos.svc.repos.mdb.TasksRepo;
import org.pubcoi.fos.svc.repos.mdb.UserObjectFlagRepo;
import org.pubcoi.fos.svc.rest.UI;
import org.pubcoi.fos.svc.services.OCRestSvc;
import org.pubcoi.fos.svc.services.ScheduledSvc;
import org.pubcoi.fos.svc.services.TransactionOrchestrationSvc;
import org.pubcoi.fos.svc.services.Utils;
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
    final OrganisationsMDBRepo orgMDBRepo;
    final OrganisationsGraphRepo organisationsGraphRepo;
    final OCRestSvc ocRestSvc;
    final UserObjectFlagRepo objectFlagsRepo;
    final ScheduledSvc scheduledSvc;

    public Tasks(
            TasksRepo tasksRepo,
            TransactionOrchestrationSvc transactionOrch,
            FosUserRepo userRepo,
            ClientsGraphRepo clientGRepo,
            OrganisationsMDBRepo orgMDBRepo,
            OrganisationsGraphRepo organisationsGraphRepo,
            OCRestSvc ocRestSvc, UserObjectFlagRepo objectFlagsRepo, ScheduledSvc scheduledSvc) {
        this.tasksRepo = tasksRepo;
        this.transactionOrch = transactionOrch;
        this.userRepo = userRepo;
        this.clientGRepo = clientGRepo;
        this.orgMDBRepo = orgMDBRepo;
        this.organisationsGraphRepo = organisationsGraphRepo;
        this.ocRestSvc = ocRestSvc;
        this.objectFlagsRepo = objectFlagsRepo;
        this.scheduledSvc = scheduledSvc;
    }

    /**
     * Searches OpenCorporates for companies matching a particular set of terms
     * @param requestDAO the request object
     * @param authToken the user auth token
     * @return a set of matching results
     *
     */
    @PostMapping("/api/ui/tasks/verify_company/_search")
    public List<VerifyCompanySearchResponse> doCompanyVerifySearch(
            @RequestBody VerifyCompanySearchRequestDAO requestDAO,
            @RequestHeader String authToken
    ) {
        String uid = UI.checkAuth(authToken).getUid();
        FosUser user = userRepo.getByUid(uid);
        logger.debug("Performing search on behalf of {}", user);
        OrganisationNode org = organisationsGraphRepo.findOrgNotHydratingPersons(requestDAO.getCompanyId()).orElseThrow();
        OCWrapper wrapper = ocRestSvc.doCompanySearch(org.getName());
        return wrapper.getResults().getCompanies().stream()
                .map(company -> new VerifyCompanySearchResponse(company))
                .peek(response -> response.setFlagged(objectFlagsRepo.existsByEntityIdAndUid(response.getId(), user.getUid())))
                .collect(Collectors.toList());
    }

    @GetMapping("/api/ui/tasks")
    public List<TaskDAO> getTasks(@RequestParam(value = "completed", defaultValue = "false") Boolean completed) {
        return tasksRepo.findAll().stream()
                .filter(t -> t.getCompleted().equals(completed))
                .map(TaskDAO::new)
                .peek(task -> {
                    if (task.getTaskType().equals(FosTaskType.resolve_client)) {
                        Optional<ClientNode> clientNode = clientGRepo.findByIdEquals(task.getEntity());
                        if (!clientNode.isPresent()) {
                            logger.error("Unable to find ClientNode {}", task.getEntity());
                            task = null;
                        } else {
                            task.setDescription(String.format(
                                    "Verify details for entity: %s", clientNode.get().getName())
                            );
                        }
                    } else if (task.getTaskType().equals(FosTaskType.resolve_company)) {
                        logger.debug("Resolving task details for entity {}", task.getEntity());
                        Optional<FosOrganisation> org = orgMDBRepo.findById(task.getEntity());
                        if (org.isPresent()) {
                            task.setDescription(String.format("Verify details for company: %s", org.get().getCompanyName()));
                        } else {
                            logger.warn("Unable to resolve task for entity {}", task.getEntity());
                            task = null;
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
    public UpdateNodeDAO updateClientDAO(
            @PathVariable FosUITasks taskType,
            @RequestHeader String authToken,
            @RequestBody RequestWithAuth req
    ) {
        FosUser user = userRepo.getByUid(UI.checkAuth(authToken).getUid());

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
            return new UpdateNodeDAO().setResponse("Resolved task: marked node as canonical");
        }

        if (taskType == FosUITasks.link_clientNode_to_parentClientNode) {
            if (null == req.getSource() || null == req.getTarget() || null == req.getTaskId()) {
                throw new FosBadRequestException("Task ID, Source and Target must be populated");
            }

            ClientNode source = clientGRepo.findClientHydratingNotices(req.getSource()).orElseThrow();
            ClientNode target = clientGRepo.findClientHydratingNotices(req.getTarget()).orElseThrow();

            transactionOrch.exec(FosTransactionBuilder.linkSourceToParent(source, target, user, null));

            markTaskCompleted(req.getTaskId(), user);

            return new UpdateNodeDAO().setResponse(String.format(
                    "Resolved task: linked %s to %s", req.getSource(), req.getTarget()
            ));
        }

        if (taskType == FosUITasks.verify_company) {
            if (null == req.getSource() || null == req.getTarget()) {
                throw new FosBadRequestException("Source and target must be populated");
            }

            DRTask task = tasksRepo.getByTaskTypeAndEntity(FosTaskType.resolve_company, orgMDBRepo.findById(req.getSource()).orElseThrow());
            if (null == task) {
                throw new FosBadRequestException("Unable to find relevant task");
            }

            // looking up target ensures we have it in db
            OCCompanySchema companySchema = scheduledSvc.getCompany(req.getTarget());

            OrganisationNode source = organisationsGraphRepo.findById(req.getSource()).orElseThrow();
            OrganisationNode target = organisationsGraphRepo
                    .findById(Utils.convertOCCompanyToGraphID(req.getTarget()))
                    .orElse(organisationsGraphRepo.save(new OrganisationNode(companySchema)));

            transactionOrch.exec(FosTransactionBuilder.resolveCompany(source, target, user, null));
            markTaskCompleted(task.getId(), user);
            return new UpdateNodeDAO().setResponse(String.format("Linked %s to %s", source.getId(), target.getId()));
        }

        throw new FosBadRequestException("Unable to find request type");
    }

    private void markTaskCompleted(String taskId, FosUser user) {
        tasksRepo.save(tasksRepo.getById(taskId).setCompleted(true).setCompletedBy(user).setCompletedDT(OffsetDateTime.now()));
    }

}
