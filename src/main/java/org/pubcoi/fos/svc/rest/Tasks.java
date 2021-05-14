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

import com.opencorporates.schemas.OCCompanySchema;
import org.pubcoi.fos.svc.exceptions.core.FosCoreException;
import org.pubcoi.fos.svc.exceptions.core.FosCoreRecordNotFoundException;
import org.pubcoi.fos.svc.exceptions.endpoint.FosEndpointBadRequestException;
import org.pubcoi.fos.svc.exceptions.endpoint.FosEndpointException;
import org.pubcoi.fos.svc.models.core.*;
import org.pubcoi.fos.svc.models.dto.*;
import org.pubcoi.fos.svc.models.dto.tasks.ResolvePotentialCOIDTO;
import org.pubcoi.fos.svc.models.dto.tasks.ResolvedCOIDTOResponse;
import org.pubcoi.fos.svc.models.dto.tasks.TaskDTO;
import org.pubcoi.fos.svc.models.dto.tasks.UpdateNodeDTO;
import org.pubcoi.fos.svc.models.es.MemberInterest;
import org.pubcoi.fos.svc.models.neo.nodes.ClientNode;
import org.pubcoi.fos.svc.models.neo.nodes.OrganisationNode;
import org.pubcoi.fos.svc.models.neo.nodes.PersonNode;
import org.pubcoi.fos.svc.models.oc.OCWrapper;
import org.pubcoi.fos.svc.repos.es.MembersInterestsESRepo;
import org.pubcoi.fos.svc.repos.gdb.jpa.ClientsGraphRepo;
import org.pubcoi.fos.svc.repos.gdb.jpa.OrganisationsGraphRepo;
import org.pubcoi.fos.svc.repos.gdb.jpa.PersonsGraphRepo;
import org.pubcoi.fos.svc.repos.mdb.OrganisationsMDBRepo;
import org.pubcoi.fos.svc.repos.mdb.TasksMDBRepo;
import org.pubcoi.fos.svc.repos.mdb.UserObjectFlagRepo;
import org.pubcoi.fos.svc.services.*;
import org.pubcoi.fos.svc.services.auth.FosAuthProvider;
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

    final FosAuthProvider authProvider;
    final TasksMDBRepo tasksMDBRepo;
    final TransactionOrchestrationSvc transactionOrch;
    final ClientsGraphRepo clientGRepo;
    final OrganisationsMDBRepo orgMDBRepo;
    final OrganisationsGraphRepo organisationsGraphRepo;
    final OCRestSvc ocRestSvc;
    final UserObjectFlagRepo objectFlagsRepo;
    final ScheduledSvc scheduledSvc;
    final MembersInterestsESRepo membersInterestsESRepo;
    final PersonsGraphRepo personsGraphRepo;

    public Tasks(
            FosAuthProvider authProvider,
            TasksMDBRepo tasksMDBRepo,
            TransactionOrchestrationSvc transactionOrch,
            ClientsGraphRepo clientGRepo,
            OrganisationsMDBRepo orgMDBRepo,
            OrganisationsGraphRepo organisationsGraphRepo,
            OCRestSvc ocRestSvc,
            UserObjectFlagRepo objectFlagsRepo,
            ScheduledSvc scheduledSvc,
            MembersInterestsESRepo membersInterestsESRepo,
            PersonsGraphRepo personsGraphRepo) {
        this.authProvider = authProvider;
        this.tasksMDBRepo = tasksMDBRepo;
        this.transactionOrch = transactionOrch;
        this.clientGRepo = clientGRepo;
        this.orgMDBRepo = orgMDBRepo;
        this.organisationsGraphRepo = organisationsGraphRepo;
        this.ocRestSvc = ocRestSvc;
        this.objectFlagsRepo = objectFlagsRepo;
        this.scheduledSvc = scheduledSvc;
        this.membersInterestsESRepo = membersInterestsESRepo;
        this.personsGraphRepo = personsGraphRepo;
    }

    /**
     * Searches OpenCorporates for companies matching a particular set of terms
     *
     * @param requestDTO the request object
     * @param authToken  the user auth token
     * @return a set of matching results
     */
    @PostMapping("/api/ui/tasks/verify_company/_search")
    public List<VerifyCompanySearchResponse> doCompanyVerifySearch(
            @RequestBody VerifyCompanySearchRequestDTO requestDTO,
            @RequestHeader String authToken
    ) {
        String uid = authProvider.getUid(authToken);
        FosUser user = authProvider.getByUid(uid);
        logger.debug("Performing search on behalf of {}", user);
        OrganisationNode org = organisationsGraphRepo.findByFosId(requestDTO.getCompanyId()).orElseThrow();
        try {
            OCWrapper wrapper = ocRestSvc.doCompanySearch(org.getName());
            return wrapper.getResults().getCompanies().stream()
                    .map(company -> new VerifyCompanySearchResponse(company))
                    .peek(response -> response.setFlagged(objectFlagsRepo.existsByEntityIdAndUid(response.getId(), user.getUid())))
                    .collect(Collectors.toList());
        } catch (FosCoreException e) {
            logger.error("Unable to perform search");
            throw new FosEndpointException(e);
        }
    }

    @GetMapping("/api/ui/tasks")
    public List<TaskDTO> getTasks(@RequestParam(value = "completed", defaultValue = "false") Boolean completed) {
        return tasksMDBRepo.findAll().stream()
                .filter(t -> t.getCompleted().equals(completed))
                .map(TaskDTO::new)
                .peek(task -> {
                    if (task.getTaskType().equals(FosTaskType.resolve_client)) {
                        Optional<ClientNode> clientNode = clientGRepo.findByFosId(task.getEntity());
                        if (!clientNode.isPresent()) {
                            logger.error("Unable to find ClientNode {}", task.getEntity());
                            task = null;
                        } else {
                            task.setDescription(String.format("Verify details for entity: %s", clientNode.get().getName()));
                        }
                    } else if (task.getTaskType().equals(FosTaskType.resolve_company)) {
                        Optional<FosOrganisation> org = orgMDBRepo.findById(task.getEntity());
                        if (org.isPresent()) {
                            task.setDescription(String.format("Verify details for company: %s", org.get().getCompanyName()));
                        } else {
                            logger.warn("Unable to resolve task for entity {}", task.getEntity());
                            task = null;
                        }
                    } else if (task.getTaskType().equals(FosTaskType.resolve_potential_coi)) {
                        Optional<FosOrganisation> org = orgMDBRepo.findById(task.getEntity());
                        if (org.isPresent()) {
                            task.setDescription(String.format("Resolve potential conflict of interest for organisation %s", org.get().getCompanyName()));
                        } else {
                            logger.warn("Unable to resolve task for entity {}", task.getEntity());
                            task = null;
                        }
                    }
                })
                .collect(Collectors.toList());
    }

    @PutMapping("/api/ui/tasks")
    public CreateTaskResponseDTO createTask(
            @RequestBody CreateTaskRequestDTO createTask,
            @RequestHeader("authToken") String authToken
    ) {
        authProvider.getUid(authToken);
        logger.debug("{}", createTask);
        return new CreateTaskResponseDTO().setTaskId(UUID.randomUUID().toString());
    }

    @GetMapping("/api/ui/tasks/resolve_client/{refId}")
    public ResolveClientDTO getTaskDetails(@PathVariable("refId") String refID) {
        return new ResolveClientDTO(clientGRepo.findByFosId(refID).orElseThrow(() -> new FosEndpointException("Unable to find entity")));
    }

    @GetMapping("/api/ui/tasks/resolve_potential_coi/{taskId}")
    public ResolvePotentialCOIDTO getCOITaskDetails(@PathVariable("taskId") String taskId) {
        DRTask task = tasksMDBRepo.findById(taskId).orElseThrow();
        if (task instanceof DRResolvePotentialCOITask) {
            MemberInterest interest = membersInterestsESRepo.findById(((DRResolvePotentialCOITask) task).getLinkedId()).orElseThrow();
            FosOrganisation org = orgMDBRepo.findById(task.getEntity().getFosId()).orElseThrow();
            return new ResolvePotentialCOIDTO(task, org, interest);
        }
        throw new FosEndpointBadRequestException();
    }

    @PutMapping("/api/ui/tasks/resolve_potential_coi/{taskId}/{action}")
    public ResolvedCOIDTOResponse ignorePotentialCOI(@PathVariable("taskId") String taskId, @PathVariable String action, @RequestHeader("authToken") String authToken) {
        FosUser currentUser = authProvider.getByUid(authProvider.getUid(authToken));
        DRTask task = tasksMDBRepo.findById(taskId).orElseThrow();
        if (!(task instanceof DRResolvePotentialCOITask))
            throw new FosEndpointBadRequestException("Not correct task type");
        OrganisationNode organisationNode = organisationsGraphRepo.findByFosId(task.getEntity().getFosId()).orElseThrow();
        MemberInterest interest = membersInterestsESRepo.findById(((DRResolvePotentialCOITask) task).getLinkedId()).orElseThrow();
        PersonNode personNode = personsGraphRepo.findByFosId(interest.getPersonNodeId()).orElseThrow();
        if (action.equals("flag")) {
            transactionOrch.exec(FosTransactionBuilder.markPotentialCOI(personNode, organisationNode, currentUser));
        }
        tasksMDBRepo.save(task.setCompleted(true).setCompletedBy(currentUser).setCompletedDT(OffsetDateTime.now()));
        Optional<DRTask> nextTask = tasksMDBRepo.findAllByTaskType(FosTaskType.resolve_potential_coi).stream().filter(c -> !c.getCompleted()).findFirst();
        return new ResolvedCOIDTOResponse(nextTask.orElse(null));
    }

    @PutMapping(value = "/api/ui/tasks/{taskType}", consumes = "application/json")
    public UpdateNodeDTO updateClientDTO(
            @PathVariable FosUITasks taskType,
            @RequestHeader String authToken,
            @RequestBody RequestWithAuth req
    ) {
        FosUser user = authProvider.getByUid(authProvider.getUid(authToken));

        /* ******** IMPORTANT ***********
         * This may look a bit convoluted in the fact that we're constructing transactions
         * here to be managed by the TransactionOrchestrationSvc but there's a good reason
         * for that - by coupling loosely, and enforcing transactions to be built via the
         * FosTransactionBuilder, we make it easier for transactions to be 'replayed' in
         * future on other systems.
         */

        if (taskType == FosUITasks.mark_canonical_clientNode) {
            if (null == req.getTaskId() || null == req.getTarget()) {
                throw new FosEndpointBadRequestException("Task ID and target must not be null");
            }

            clientGRepo.findByFosId(req.getTarget()).ifPresent(clientNode -> {
                transactionOrch.exec(FosTransactionBuilder.markCanonicalNode(clientNode, user, null));
            });

            markTaskCompleted(req.getTaskId(), user);
            return new UpdateNodeDTO().setResponse("Resolved task: marked node as canonical");
        }

        if (taskType == FosUITasks.link_clientNode_to_parentClientNode) {
            if (null == req.getSource() || null == req.getTarget() || null == req.getTaskId()) {
                throw new FosEndpointBadRequestException("Task ID, Source and Target must be populated");
            }

            ClientNode source = clientGRepo.findClientHydratingNotices(req.getSource()).orElseThrow();
            ClientNode target = clientGRepo.findClientHydratingNotices(req.getTarget()).orElseThrow();

            transactionOrch.exec(FosTransactionBuilder.linkSourceToParent(source, target, user, null));

            markTaskCompleted(req.getTaskId(), user);

            return new UpdateNodeDTO().setResponse(String.format(
                    "Resolved task: linked %s to %s", req.getSource(), req.getTarget()
            ));
        }

        if (taskType == FosUITasks.verify_company) {
            if (null == req.getSource() || null == req.getTarget()) {
                throw new FosEndpointBadRequestException("Source and target must be populated");
            }

            FosOrganisation org = orgMDBRepo.findById(req.getSource()).orElseThrow();
            DRTask task = tasksMDBRepo.findByTaskTypeAndEntity(FosTaskType.resolve_company, org)
                    .orElseGet(() -> {
                        logger.warn("Task does not exist - creating ad-hoc task to resolve company {}", org);
                        return new DRTask(FosTaskType.resolve_company, org);
                    });

            // looking up target ensures we have it in db
            OCCompanySchema companySchema;
            try {
                companySchema = scheduledSvc.getCompany(req.getTarget());
            } catch (FosCoreRecordNotFoundException e) {
                throw new FosEndpointBadRequestException(e.getMessage());
            }

            OrganisationNode source = organisationsGraphRepo
                    .findByFosId(req.getSource()).orElseThrow();
            OrganisationNode target = organisationsGraphRepo
                    .findByFosId(Utils.convertOCCompanyToGraphID(req.getTarget())).orElseGet(() -> {
                        logger.info(Ansi.Yellow.format("Did not find OrganisationNode %s: instantiating new instance", req.getTarget()));
                        return organisationsGraphRepo.save(new OrganisationNode(companySchema));
                    });

            transactionOrch.exec(FosTransactionBuilder.resolveCompany(source, target, user, null));
            markTaskCompleted(task.getId(), user);
            return new UpdateNodeDTO().setResponse(String.format("Linked %s to %s", source.getFosId(), target.getFosId()));
        }

        throw new FosEndpointBadRequestException("Unable to find request type");
    }

    private void markTaskCompleted(String taskId, FosUser user) {
        tasksMDBRepo.save(tasksMDBRepo.getById(taskId).setCompleted(true).setCompletedBy(user).setCompletedDT(OffsetDateTime.now()));
    }

}
