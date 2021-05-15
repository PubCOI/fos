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

import org.neo4j.driver.Value;
import org.pubcoi.fos.svc.models.core.CFAward;
import org.pubcoi.fos.svc.models.dto.PotentialConflictDTO;
import org.pubcoi.fos.svc.models.queries.AwardsGraphListResponseDTO;
import org.pubcoi.fos.svc.models.queries.ClientsGraphListResponseDTO;
import org.pubcoi.fos.svc.models.queries.OrganisationsGraphListResponseDTO;
import org.pubcoi.fos.svc.repos.es.MembersInterestsESRepo;
import org.pubcoi.fos.svc.repos.gdb.custom.AwardsListRepo;
import org.pubcoi.fos.svc.repos.gdb.custom.OrganisationsGraphCustomRepo;
import org.pubcoi.fos.svc.repos.gdb.jpa.ClientsGraphRepo;
import org.pubcoi.fos.svc.repos.gdb.jpa.OrganisationsGraphRepo;
import org.pubcoi.fos.svc.repos.gdb.jpa.PersonsGraphRepo;
import org.pubcoi.fos.svc.repos.mdb.AwardsMDBRepo;
import org.pubcoi.fos.svc.repos.mdb.PotentialConflictsRepo;
import org.pubcoi.fos.svc.services.auth.FosAuthProvider;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class DatasetsRest {

    final AwardsListRepo awardsListRepo;
    final AwardsMDBRepo awardsMDBRepo;
    final ClientsGraphRepo clientsGraphRepo;
    final OrganisationsGraphRepo organisationsGraphRepo;
    final OrganisationsGraphCustomRepo organisationsGraphCustomRepo;
    final PotentialConflictsRepo potentialConflictsRepo;
    final FosAuthProvider fosAuthProvider;
    final MembersInterestsESRepo membersInterestsESRepo;
    final PersonsGraphRepo personsGraphRepo;

    public DatasetsRest(AwardsListRepo awardsListRepo,
                        AwardsMDBRepo awardsMDBRepo,
                        ClientsGraphRepo clientsGraphRepo,
                        OrganisationsGraphRepo organisationsGraphRepo,
                        OrganisationsGraphCustomRepo organisationsGraphCustomRepo,
                        PotentialConflictsRepo potentialConflictsRepo,
                        FosAuthProvider fosAuthProvider,
                        MembersInterestsESRepo membersInterestsESRepo,
                        PersonsGraphRepo personsGraphRepo) {
        this.awardsListRepo = awardsListRepo;
        this.awardsMDBRepo = awardsMDBRepo;
        this.clientsGraphRepo = clientsGraphRepo;
        this.organisationsGraphRepo = organisationsGraphRepo;
        this.organisationsGraphCustomRepo = organisationsGraphCustomRepo;
        this.potentialConflictsRepo = potentialConflictsRepo;
        this.fosAuthProvider = fosAuthProvider;
        this.membersInterestsESRepo = membersInterestsESRepo;
        this.personsGraphRepo = personsGraphRepo;
    }

    @GetMapping("/api/datasets/awards")
    public List<AwardsGraphListResponseDTO> getContractAwards() {
        return awardsListRepo.getAwardsWithRels()
                .stream().map(AwardsGraphListResponseDTO::new)
                .filter(a -> awardsMDBRepo.existsById(a.getId())) // todo show info stats for awards that are missing
                .peek(a -> {
                    CFAward award = awardsMDBRepo.findById(a.getId()).orElseThrow();
                    a.setValueMin(award.getValueMin());
                    a.setValueMax(award.getValueMax());
                    a.setNoticeId(award.getNoticeId());
                })
                .collect(Collectors.toList());
    }

    @GetMapping("/api/datasets/clients")
    public List<ClientsGraphListResponseDTO> getClients() {
        return clientsGraphRepo.getClientsWithRels()
                .stream().map(ClientsGraphListResponseDTO::new)
                .collect(Collectors.toList());
    }

    @GetMapping("/api/datasets/organisations")
    public List<OrganisationsGraphListResponseDTO> getOrganisations() {
        return organisationsGraphCustomRepo.findOrgsWithAwardIds().stream()
                .map(entry -> new OrganisationsGraphListResponseDTO(
                        entry.getOrganisation().asMap(),
                        entry.getAwards().size(),
                        entry.getAwards().asList().stream().map(a -> (String) a).collect(Collectors.toList()),
                        awardsMDBRepo.findAllById(entry.getAwards().values(Value::asString))
                )).collect(Collectors.toList());
    }

    @GetMapping("/api/datasets/conflicts")
    public List<PotentialConflictDTO> getConflicts() {
        // this is very brittle at the moment ... but we're only storing one type of conflict atmo (person->org)
        return potentialConflictsRepo.findAll().stream()
                .map(c -> new PotentialConflictDTO(
                        c, fosAuthProvider.getByUid(c.getResolvedByUid()), personsGraphRepo.findByFosId(c.getSourceId()).orElseThrow(), organisationsGraphRepo.findByFosId(c.getTargetId()).orElseThrow())
                ).collect(Collectors.toList());
    }
}
