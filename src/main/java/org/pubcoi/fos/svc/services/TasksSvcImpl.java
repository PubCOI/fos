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

package org.pubcoi.fos.svc.services;

import org.pubcoi.fos.svc.exceptions.endpoint.FosEndpointBadRequestException;
import org.pubcoi.fos.svc.models.core.DRResolvePotentialCOITask;
import org.pubcoi.fos.svc.models.core.DRTask;
import org.pubcoi.fos.svc.models.core.FosUser;
import org.pubcoi.fos.svc.models.dto.tasks.ResolveCOIActionEnum;
import org.pubcoi.fos.svc.models.es.MemberInterest;
import org.pubcoi.fos.svc.models.mdb.PotentialConflict;
import org.pubcoi.fos.svc.models.neo.nodes.OrganisationNode;
import org.pubcoi.fos.svc.models.neo.nodes.PersonNode;
import org.pubcoi.fos.svc.repos.es.MembersInterestsESRepo;
import org.pubcoi.fos.svc.repos.gdb.jpa.OrganisationsGraphRepo;
import org.pubcoi.fos.svc.repos.gdb.jpa.PersonsGraphRepo;
import org.pubcoi.fos.svc.repos.mdb.PotentialConflictsRepo;
import org.pubcoi.fos.svc.repos.mdb.TasksMDBRepo;
import org.pubcoi.fos.svc.transactions.FosTransactionBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;

@Service
public class TasksSvcImpl implements TasksSvc {
    private static final Logger logger = LoggerFactory.getLogger(TasksSvcImpl.class);

    final TasksMDBRepo tasksMDBRepo;
    final OrganisationsGraphRepo organisationsGraphRepo;
    final MembersInterestsESRepo membersInterestsESRepo;
    final PersonsGraphRepo personsGraphRepo;
    final TransactionOrchestrationSvc transactionOrchestrationSvc;
    final PotentialConflictsRepo potentialConflictsRepo;

    public TasksSvcImpl(TasksMDBRepo tasksMDBRepo,
                        OrganisationsGraphRepo organisationsGraphRepo,
                        MembersInterestsESRepo membersInterestsESRepo,
                        PersonsGraphRepo personsGraphRepo,
                        TransactionOrchestrationSvc transactionOrchestrationSvc,
                        PotentialConflictsRepo potentialConflictsRepo) {
        this.tasksMDBRepo = tasksMDBRepo;
        this.organisationsGraphRepo = organisationsGraphRepo;
        this.membersInterestsESRepo = membersInterestsESRepo;
        this.personsGraphRepo = personsGraphRepo;
        this.transactionOrchestrationSvc = transactionOrchestrationSvc;
        this.potentialConflictsRepo = potentialConflictsRepo;
    }

    @Override
    public void createTask(DRTask task) {
        if (tasksMDBRepo.existsById(task.getId())) {
            logger.warn("Task already created");
        } else {
            tasksMDBRepo.save(task.setCompleted(false));
        }
    }

    @Override
    @Transactional
    public void resolvePotentialConflict(String taskId, ResolveCOIActionEnum action, FosUser currentUser) {
        DRTask task = tasksMDBRepo.findById(taskId).orElseThrow();
        if (!(task instanceof DRResolvePotentialCOITask)) {
            throw new FosEndpointBadRequestException("Not correct task type");
        }
        OrganisationNode organisationNode = organisationsGraphRepo.findByFosId(task.getEntity().getFosId()).orElseThrow();
        MemberInterest interest = membersInterestsESRepo.findById(((DRResolvePotentialCOITask) task).getLinkedId()).orElseThrow();
        PersonNode personNode = personsGraphRepo.findByFosId(interest.getPersonNodeId()).orElseThrow();
        logger.info("Processing request to {} relation between {} and {}", action, personNode, organisationNode);

        if (action.equals(ResolveCOIActionEnum.flag)) {
            transactionOrchestrationSvc.exec(FosTransactionBuilder.markPotentialCOI(personNode, organisationNode, currentUser));
        }
        task.setCompleted(true).setCompletedByUid(currentUser.getUid()).setCompletedDT(OffsetDateTime.now());
        PotentialConflict pc = new PotentialConflict((DRResolvePotentialCOITask) task, membersInterestsESRepo.findById(((DRResolvePotentialCOITask) task).getLinkedId()).orElseThrow(), action);
        logger.debug("Saving potential conflict {}", pc);
        potentialConflictsRepo.save(pc);
        tasksMDBRepo.save(task);
    }
}
