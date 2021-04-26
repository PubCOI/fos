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
import org.pubcoi.cdm.mnis.MnisInterestCategoryType;
import org.pubcoi.cdm.mnis.MnisInterestType;
import org.pubcoi.cdm.mnis.MnisMemberType;
import org.pubcoi.cdm.mnis.MnisMembersType;
import org.pubcoi.cdm.pw.PWRootType;
import org.pubcoi.cdm.pw.RegisterEntryType;
import org.pubcoi.fos.svc.exceptions.FosBadRequestException;
import org.pubcoi.fos.svc.exceptions.FosCoreException;
import org.pubcoi.fos.svc.exceptions.FosException;
import org.pubcoi.fos.svc.models.neo.nodes.DeclaredInterest;
import org.pubcoi.fos.svc.models.neo.nodes.OrganisationNode;
import org.pubcoi.fos.svc.models.neo.nodes.PersonNode;
import org.pubcoi.fos.svc.models.neo.nodes.PersonNodeType;
import org.pubcoi.fos.svc.models.neo.relationships.OrgPersonLink;
import org.pubcoi.fos.svc.models.neo.relationships.PersonConflictLink;
import org.pubcoi.fos.svc.repos.gdb.custom.ClientNodeFTS;
import org.pubcoi.fos.svc.repos.gdb.jpa.ClientsGraphRepo;
import org.pubcoi.fos.svc.repos.gdb.jpa.DeclaredInterestRepo;
import org.pubcoi.fos.svc.repos.gdb.jpa.OrganisationsGraphRepo;
import org.pubcoi.fos.svc.repos.gdb.jpa.PersonsGraphRepo;
import org.pubcoi.fos.svc.repos.mdb.*;
import org.pubcoi.fos.svc.services.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.pubcoi.fos.svc.services.Utils.mnisIdHash;

@Profile("debug")
@RestController
public class Debug {
    private static final Logger logger = LoggerFactory.getLogger(Debug.class);

    final AttachmentMDBRepo attachmentMDBRepo;
    final NoticesMDBRepo noticesMDBRepo;
    final OCCompaniesRepo ocCompanies;
    final GraphSvc graphSvc;
    final ScheduledSvc scheduledSvc;
    final ClientNodeFTS clientNodeFTS;
    final TransactionOrchestrationSvc transactionOrchestrationSvc;
    final TasksRepo tasksRepo;
    final ClientsGraphRepo clientsGraphRepo;
    final PersonsGraphRepo personsGraphRepo;
    final OrganisationsGraphRepo organisationsGraphRepo;
    final MnisMembersRepo mnisMembersRepo;
    final MnisSvc mnisSvc;
    final DeclaredInterestRepo declaredInterestRepo;
    final XslSvc xslSvc;
    final RestTemplate restTemplate;

    public Debug(
            AttachmentMDBRepo attachmentMDBRepo,
            NoticesMDBRepo noticesMDBRepo,
            OCCompaniesRepo ocCompanies,
            GraphSvc graphSvc,
            ScheduledSvc scheduledSvc,
            ClientNodeFTS clientNodeFTS,
            TransactionOrchestrationSvc transactionOrchestrationSvc,
            TasksRepo tasksRepo,
            ClientsGraphRepo clientsGraphRepo,
            PersonsGraphRepo personsGraphRepo,
            OrganisationsGraphRepo organisationsGraphRepo,
            MnisMembersRepo mnisMembersRepo,
            MnisSvc mnisSvc,
            DeclaredInterestRepo declaredInterestRepo,
            XslSvc xslSvc,
            RestTemplate restTemplate) {
        this.attachmentMDBRepo = attachmentMDBRepo;
        this.noticesMDBRepo = noticesMDBRepo;
        this.ocCompanies = ocCompanies;
        this.graphSvc = graphSvc;
        this.scheduledSvc = scheduledSvc;
        this.clientNodeFTS = clientNodeFTS;
        this.transactionOrchestrationSvc = transactionOrchestrationSvc;
        this.tasksRepo = tasksRepo;
        this.clientsGraphRepo = clientsGraphRepo;
        this.personsGraphRepo = personsGraphRepo;
        this.organisationsGraphRepo = organisationsGraphRepo;
        this.mnisMembersRepo = mnisMembersRepo;
        this.mnisSvc = mnisSvc;
        this.declaredInterestRepo = declaredInterestRepo;
        this.xslSvc = xslSvc;
        this.restTemplate = restTemplate;
    }

    @Value("${pubcoi.fos.apis.parliament.commons-list}")
    String commonsDataURL;

    @Value("${pubcoi.fos.apis.parliament.lords-list}")
    String lordsDataURL;

    @GetMapping("/api/oc-companies")
    public List<OCCompanySchema> getCompanies() {
        return ocCompanies.findAll();
    }

    @GetMapping("/api/debug/populate-companies")
    public void populateCompanies() {
        scheduledSvc.populateFosOrgsMDBFromAwards();
    }

    @DeleteMapping("/api/debug/clear-mdb")
    public void clearMDB() {
        transactionOrchestrationSvc.clearTransactions();
        tasksRepo.deleteAll();
    }

    @PutMapping("/api/debug/populate-all")
    public void populateAll() {
        populateCompanies();
        populateGraph();
        logger.info("Complete");
    }

    @GetMapping("/api/debug/populate-graph")
    public void populateGraph() {
        graphSvc.populateGraphFromMDB();
    }

    /**
     * DEBUG CLASS
     * <p>
     * calls list of companies
     * pulls back an officer
     * inserts officer and links to company
     */
    @PutMapping("/api/debug/populate-office-bearers")
    public void populateOfficeBearers() {
        organisationsGraphRepo.findAllNotHydrating().forEach(organisationNode -> {
            if (null == organisationNode.getReference() || null == organisationNode.getJurisdiction()) {
                logger.info("{} not an OC node, skipping", organisationNode);
                return;
            }
            OCCompanySchema companySchema = ocCompanies.findByCompanyNumberAndJurisdictionCode(organisationNode.getReference(), organisationNode.getJurisdiction());
            companySchema.getOfficers().forEach(officer -> {
                logger.debug("Looking up details for officer {} (fosId:{})",
                        PersonNode.convertPersonOCIdToString(officer.getOfficer().getId()),
                        PersonNode.generatePersonId(officer.getOfficer())
                );
                // warning don't use in production - won't add people to two different companies
                // if (!personsGraphRepo.existsByOcId(getUID(officer.getOfficer().getId()))) { // todo add getid to aspects
                final String orgId = String.format("%s:%s", companySchema.getJurisdictionCode(), companySchema.getCompanyNumber());
                logger.debug("Looking up org details for {}", orgId);

                // first try find org using stated ID
                Optional<OrganisationNode> orgOpt = organisationsGraphRepo
                        // first look for a version WITH persons
                        .findOrgHydratingPersons(orgId);
                // if that search didn't work, search again but without hydrating ...
                // todo: check whether we might be able to get away with not doing this given we're now searching
                // orgPersonLink explicitly via separate exists() query
                if (orgOpt.isEmpty()) {
                    orgOpt = organisationsGraphRepo.findOrgNotHydratingPersons(orgId);
                }
                if (orgOpt.isEmpty()) {
                    throw new FosBadRequestException(String.format("Unable to find organisation %s", orgId));
                }
                OrganisationNode org = orgOpt.get();

                final String transactionId = UUID.randomUUID().toString();

                PersonNode personNode = personsGraphRepo
                        .findByFosId(PersonNode.generatePersonId(officer.getOfficer()))
                        .orElse(new PersonNode(PersonNodeType.OfficeBearer, officer.getOfficer(), transactionId));

                if (!organisationsGraphRepo.relationshipExists(org.getFosId(), personNode.getFosId())) {
                    logger.debug("Instantiating new relationship between {} and {}", org, personNode);
                    OrgPersonLink orgPersonLink = new OrgPersonLink(personNode,
                            org.getFosId(),
                            officer.getOfficer().getPosition(),
                            getZDT(officer.getOfficer().getStartDate()),
                            getZDT(officer.getOfficer().getEndDate()), transactionId);
                    org.getOrgPersons().add(orgPersonLink);
                    logger.trace("Completed adding person {} to organisation {}", personNode.getFosId(), org.getFosId());
                }
                organisationsGraphRepo.save(org);
            });
        });
    }

    /**
     * This is taking data from PublicWhip data dumps
     * @return OK if successful
     */
    @PostMapping("/api/datasets/members-interests")
    public String uploadMembersInterests(@RequestBody String input, @RequestParam("dataset") String dataset) {
        PWRootType cleaned = xslSvc.cleanPWData(input);
        for (RegisterEntryType register : cleaned.getRegisters()) {
            try {
                mnisSvc.addInterestsToMDB(register, dataset);
            } catch (FosCoreException e) {
                throw new FosException();
            }
        }
        return "ok";
    }

    @PostMapping("/api/datasets/members-interests/reindex")
    public void reindexInterests() {
        mnisSvc.reindex();
    }

    @PostMapping("/api/datasets/members-interests/reanalyse")
    public void reanalyseInterests() {
        mnisSvc.reanalyse();
    }

    @PostMapping("/api/datasets/politicians")
    public String uploadMnisData(MultipartHttpServletRequest request) {
        MultipartFile file = request.getFile("file");
        if (null == file) {
            throw new FosBadRequestException("Empty file");
        }
        try {
            JAXBContext context = JAXBContext.newInstance(MnisMembersType.class);
            Unmarshaller u = context.createUnmarshaller();
            MnisMembersType array = (MnisMembersType) u.unmarshal(new ByteArrayInputStream(file.getBytes()));
            for (MnisMemberType mnisMemberType : array.getMembers()) {
                logger.debug("Adding member {}", mnisMemberType.getMemberId());
                mnisMembersRepo.save(mnisMemberType);
            }
        } catch (IOException | JAXBException e) {
            throw new FosException("Unable to read file stream");
        }
        return "ok";
    }

    /**
     *
     * Used to do an initial load of politician data (names etc)
     */
    @PostMapping("/api/datasets/politicians/bootstrap")
    public void bootstrapPolData() {

        logger.info("Loading data from commons endpoint");
        loadMembersData(commonsDataURL);

        logger.info("Loading data from lords endpoint");
        loadMembersData(lordsDataURL);

        logger.info("Done");
    }

    private void loadMembersData(String dataUrl) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_XML));
        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<MnisMembersType> commonsResponse = restTemplate
                .exchange(dataUrl, HttpMethod.GET, entity, MnisMembersType.class);
        if (null != commonsResponse.getBody()) {
            for (MnisMemberType member : commonsResponse.getBody().getMembers()) {
                if (!mnisMembersRepo.existsById(member.getMemberId())) {
                    mnisMembersRepo.save(member);
                }
            }
        }
    }

    @GetMapping("/api/datasets/politicians/populate-interests")
    public String populateOnePolitician() {
        mnisMembersRepo.findAll().stream()
                .filter(p -> null == p.getInterests())
                .forEach(m -> mnisSvc.populateInterestsForMember(m.getMemberId()));
        return "ok";
    }

    @GetMapping("/api/datasets/politicians/populate-graph")
    public void populatePolGraph() {
        mnisMembersRepo.findAll()
                .forEach(m -> {
                    PersonNode p = new PersonNode(m);
                    if (!personsGraphRepo.existsByParliamentaryId(m.getMemberId())) {
                        logger.debug("Saving person {}", p);
                        personsGraphRepo.save(p);
                    }
                    boolean changed = false;
                    for (MnisInterestCategoryType mnisInterestCategoryType : m.getInterests().getCategories()) {
                        for (MnisInterestType mnisInterestType : mnisInterestCategoryType.getInterests()) {
                            if (!declaredInterestRepo.existsByFosId(mnisIdHash(mnisInterestType.getId()))) {
                                logger.debug("Adding conflict {} to person {}", mnisInterestType.getId(), p.getFosId());
                                PersonConflictLink conflictLink = new PersonConflictLink(m, new DeclaredInterest(mnisInterestType));
                                if (null != mnisInterestType.getCreatedDT()) {
                                    conflictLink.setStartDT(mnisInterestType.getCreatedDT().toZonedDateTime());
                                }
                                if (null != mnisInterestType.getDeletedDT()) {
                                    conflictLink.setEndDT(mnisInterestType.getDeletedDT().toZonedDateTime());
                                }
                                p.getConflicts().add(conflictLink);
                                changed = true;
                            }
                        }
                    }
                    if (changed) {
                        logger.debug("Saving changes to {}", p.getFosId());
                        personsGraphRepo.save(p);
                    }
                });
    }

    private LocalDate getDate(Object date) {
        if (null == date) {
            return null;
        }
        if (date instanceof String) {
            return LocalDate.parse((CharSequence) date);
        }
        throw new IllegalArgumentException("Must provide valid date string");
    }

    private ZonedDateTime getZDT(Object date) {
        LocalDate localDate = getDate(date);
        if (null == localDate) {
            return null;
        }
        return localDate.atStartOfDay(ZoneOffset.UTC);
    }
}

