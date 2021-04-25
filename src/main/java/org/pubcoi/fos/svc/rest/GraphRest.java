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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.neo4j.driver.internal.InternalNode;
import org.neo4j.driver.internal.InternalRelationship;
import org.pubcoi.cdm.cf.FullNotice;
import org.pubcoi.fos.svc.exceptions.FosBadRequestException;
import org.pubcoi.fos.svc.models.dto.*;
import org.pubcoi.fos.svc.models.dto.fts.GenericIDNameFTSResponse;
import org.pubcoi.fos.svc.models.dto.neo.InternalNodeSerializer;
import org.pubcoi.fos.svc.models.dto.neo.InternalRelationshipSerializer;
import org.pubcoi.fos.svc.models.neo.nodes.AwardNode;
import org.pubcoi.fos.svc.models.neo.nodes.ClientNode;
import org.pubcoi.fos.svc.models.neo.nodes.OrganisationNode;
import org.pubcoi.fos.svc.models.neo.nodes.PersonNode;
import org.pubcoi.fos.svc.models.neo.relationships.ClientPersonLink;
import org.pubcoi.fos.svc.models.neo.relationships.PersonConflictLink;
import org.pubcoi.fos.svc.repos.gdb.custom.ClientNodeFTS;
import org.pubcoi.fos.svc.repos.gdb.custom.OrgNodeFTS;
import org.pubcoi.fos.svc.repos.gdb.custom.PersonNodeFTS;
import org.pubcoi.fos.svc.repos.gdb.jpa.OrganisationsGraphRepo;
import org.pubcoi.fos.svc.services.AwardsSvc;
import org.pubcoi.fos.svc.services.ClientsSvc;
import org.pubcoi.fos.svc.services.NoticesSvc;
import org.pubcoi.fos.svc.services.PersonsSvc;
import org.pubcoi.fos.svc.services.auth.FosAuthProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
public class GraphRest {
    private static final Logger logger = LoggerFactory.getLogger(GraphRest.class);

    final FosAuthProvider authProvider;
    final Neo4jClient neo4jClient;
    final ClientNodeFTS clientNodeFTS;
    final OrgNodeFTS orgNodeFTS;
    final PersonNodeFTS personNodeFTS;
    final ClientsSvc clientSvc;
    final NoticesSvc noticesSvc;
    final AwardsSvc awardsSvc;
    final PersonsSvc personsSvc;
    final OrganisationsGraphRepo organisationsGraphRepo;
    static ObjectMapper neo4jObjectMapper;

    static {
        neo4jObjectMapper = new ObjectMapper();
        SimpleModule sm = new SimpleModule();
        sm.addSerializer(InternalNode.class, new InternalNodeSerializer());
        sm.addSerializer(InternalRelationship.class, new InternalRelationshipSerializer());
        neo4jObjectMapper.registerModule(sm);
        neo4jObjectMapper.registerModule(new JavaTimeModule()); // for ZonedDateTime
        neo4jObjectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); // DT not epoch please
    }

    public GraphRest(
            FosAuthProvider authProvider,
            Neo4jClient neo4jClient,
            ClientNodeFTS clientNodeFTS,
            OrgNodeFTS orgNodeFTS,
            PersonNodeFTS personNodeFTS,
            ClientsSvc clientSvc,
            NoticesSvc noticesSvc,
            AwardsSvc awardsSvc,
            PersonsSvc personsSvc,
            OrganisationsGraphRepo organisationsGraphRepo
    ) {
        this.authProvider = authProvider;
        this.neo4jClient = neo4jClient;
        this.clientNodeFTS = clientNodeFTS;
        this.orgNodeFTS = orgNodeFTS;
        this.personNodeFTS = personNodeFTS;
        this.clientSvc = clientSvc;
        this.noticesSvc = noticesSvc;
        this.awardsSvc = awardsSvc;
        this.personsSvc = personsSvc;
        this.organisationsGraphRepo = organisationsGraphRepo;
    }

    /**
     * Return canonical client nodes that match the current client name (if any)
     *
     * @param query The search parameters
     * @return List of top responses, ordered by best -> worst match
     */
    @GetMapping("/api/graphs/_search/clients")
    public List<GenericIDNameFTSResponse> findAnyClientQuery(@RequestParam String query, @RequestParam(required = false) String currentNode) {
        if (query.isEmpty()) return new ArrayList<>();
        return clientNodeFTS.findAllCanonicalClientNodesMatching(query)
                .stream()
                .filter(c -> !c.getId().equals(currentNode))
                .limit(5)
                .map(GenericIDNameFTSResponse::new)
                .collect(Collectors.toList());
    }

    @GetMapping("/api/graphs/_search/persons")
    public List<GenericIDNameFTSResponse> findAnyPersonQuery(
            @RequestParam String query,
            @RequestParam(required = false, defaultValue = "false") Boolean details
    ) {
        if (query.isEmpty()) return new ArrayList<>();
        if (details) {
            return personNodeFTS.findAnyPersonsMatchingWithDetails(String.format("%s*", query))
                    .stream()
                    .map(n -> new GraphDetailedSearchResponseDTO(n, NodeTypeEnum.person))
                    .collect(Collectors.toList());
        } else {
            return personNodeFTS.findAnyPersonsMatching(String.format("%s*", query))
                    .stream()
                    .map(n -> new GraphSearchResponseDTO(n, NodeTypeEnum.person))
                    .collect(Collectors.toList());
        }

    }

    @GetMapping("/api/graphs/_search/organisations")
    public List<GenericIDNameFTSResponse> findAnyOrgQuery(@RequestParam String query) {
        if (query.isEmpty()) return new ArrayList<>();

        // open / wildcard
        Set<GenericIDNameFTSResponse> responses = orgNodeFTS.findAnyOrgsMatching(String.format("*%s*", query), 5).stream().
                map(r -> new GraphSearchResponseDTO(r, NodeTypeEnum.organisation))
                .collect(Collectors.toSet());

        // entire token
        responses.addAll(orgNodeFTS.findAnyOrgsMatching(query, 5).stream().
                map(r -> new GraphSearchResponseDTO(r, NodeTypeEnum.organisation))
                .collect(Collectors.toSet()));

        // now sort
        return responses.stream()
                .sorted(Comparator.comparing(GenericIDNameFTSResponse::getScore).reversed())
                .limit(10)
                .collect(Collectors.toList());
    }

    /**
     * Return client or org nodes that match the current search
     * <p>
     * Currently performs 6x searches and then returns the most relevant results ... so not remotely efficient
     * <p>
     * TODO this will currently deadlock in high load situations ... need to rewrite to a single query
     *
     * @param query The search parameters
     * @return List of top responses, ordered by best -> worst match
     */
    @GetMapping("/api/graphs/_search")
    public List<GraphDetailedSearchResponseDTO> findAnyClientOrOrgQuery(@RequestParam String query) {
        if (query.isEmpty()) return new ArrayList<>();
        Set<GraphDetailedSearchResponseDTO> response = clientNodeFTS.findAnyClientsMatching(String.format("*%s*", query), 5)
                .stream()
                .map(r -> new GraphDetailedSearchResponseDTO(r, NodeTypeEnum.client))
                .collect(Collectors.toSet());

        response.addAll(clientNodeFTS.findAnyClientsMatching(query, 5)
                .stream()
                .map(r -> new GraphDetailedSearchResponseDTO(r, NodeTypeEnum.client))
                .collect(Collectors.toSet()));

        response.addAll(orgNodeFTS.findAnyOrgsMatching(String.format("*%s*", query), 5).stream().
                map(r -> new GraphDetailedSearchResponseDTO(r, NodeTypeEnum.organisation))
                .collect(Collectors.toSet())
        );

        response.addAll(orgNodeFTS.findAnyOrgsMatching(query, 5).stream().
                map(r -> new GraphDetailedSearchResponseDTO(r, NodeTypeEnum.organisation))
                .collect(Collectors.toSet())
        );

        response.addAll(personNodeFTS.findAnyPersonsMatchingWithDetails(query).stream().
                map(r -> new GraphDetailedSearchResponseDTO(r, NodeTypeEnum.person))
                .collect(Collectors.toSet())
        );

        response.addAll(personNodeFTS.findAnyPersonsMatchingWithDetails(String.format("*%s*", query)).stream().
                map(r -> new GraphDetailedSearchResponseDTO(r, NodeTypeEnum.person))
                .collect(Collectors.toSet())
        );

        return response.stream()
                .sorted(Comparator.comparing(GraphDetailedSearchResponseDTO::getScore).reversed())
                .limit(10)
                .collect(Collectors.toList());
    }

    @GetMapping("/api/graphs/_meta/initial")
    public String getQuery(
            @RequestParam(value = "max", required = false, defaultValue = "50") String max
    ) {
        Map<String, Object> bindParams = new HashMap<>();
        bindParams.put("limit", getLimit(max, 250));
        Neo4jClient.RunnableSpecTightToDatabase response = neo4jClient.query("MATCH(c:Client)-[ref:PUBLISHED]-(n:Notice) " +
                "WHERE c.hidden=false AND ref.hidden=false AND n.hidden=false " +
                "RETURN c, ref, {neo4j_id: id(n), labels: labels(n), properties: n{.*, has_awards: exists((n)-[:AWARDS]-())}} as n" +
                " LIMIT $limit").bindAll(bindParams);
        try {
            return neo4jObjectMapper.writeValueAsString(response.fetch().all());
        } catch (JsonProcessingException e) {
            throw new FosBadRequestException();
        }
    }

    /**
     * Return awards metadata so that it can be displayed in the metadata pane
     *
     * @param awardId The award ID to look up
     * @return The award object
     */
    @GetMapping("/api/graphs/awards/{awardId}/metadata")
    public AwardDTO getAward(@PathVariable String awardId) {
        return awardsSvc.getAwardDetailsDTOWithAttachments(awardId);
    }

    @GetMapping("/api/graphs/awards/{awardId}/children")
    public String getAwardChildrenQuery(
            @RequestParam(value = "max", required = false, defaultValue = "50") String max,
            @PathVariable String awardId
    ) {
        Map<String, Object> bindParams = new HashMap<>();
        bindParams.put("limit", getLimit(max, 50));
        bindParams.put("awardId", awardId);
        Neo4jClient.RunnableSpecTightToDatabase response = neo4jClient.query(
                "MATCH (:Award {id: $awardId})-[:AWARDED_TO]-(o:Organisation) " +
                        "MATCH (o)-[ref:AWARDED_TO]-(a:Award) return o, ref, a LIMIT $limit")
                .bindAll(bindParams);
        try {
            return neo4jObjectMapper.writeValueAsString(response.fetch().all());
        } catch (JsonProcessingException e) {
            throw new FosBadRequestException();
        }
    }

    @GetMapping("/api/graphs/awards/{awardId}/parents")
    public String getAwardParentsQuery(
            @RequestParam(value = "max", required = false, defaultValue = "50") String max,
            @PathVariable String awardId
    ) {
        Map<String, Object> bindParams = new HashMap<>();
        bindParams.put("limit", getLimit(max, 50));
        bindParams.put("awardId", awardId);
        Neo4jClient.RunnableSpecTightToDatabase response = neo4jClient.query(
                "MATCH (:Award {id: $awardId})-[:AWARDS]-(n:Notice) " +
                        "MATCH (n)-[ref:AWARDS]-(a:Award) return n, ref, a LIMIT $limit")
                .bindAll(bindParams);
        try {
            return neo4jObjectMapper.writeValueAsString(response.fetch().all());
        } catch (JsonProcessingException e) {
            throw new FosBadRequestException();
        }
    }

    /**
     * Used when a user clicks on a search result on the graph
     *
     * @param clientId the client id to return details on
     * @return a neo4j object that can be read by cytoscape
     */
    @GetMapping("/api/graphs/clients/{clientId}")
    public String getSpecificClientForSearchResult(@PathVariable String clientId) {
        Map<String, Object> bindParams = new HashMap<>();
        bindParams.put("clientId", clientId);
        Neo4jClient.RunnableSpecTightToDatabase response = neo4jClient.query("MATCH(c:Client)-[ref:PUBLISHED]-(n:Notice) " +
                "WHERE c.id = $clientId " +
                "RETURN c, ref, {neo4j_id: id(n), labels: labels(n), properties: n{.*, has_awards: exists((n)-[:AWARDS]-())}} as n"
        ).bindAll(bindParams);
        try {
            return neo4jObjectMapper.writeValueAsString(response.fetch().all());
        } catch (JsonProcessingException e) {
            throw new FosBadRequestException();
        }
    }

    @GetMapping("/api/graphs/clients/{clientId}/metadata")
    public ClientNodeDTO getClient(@PathVariable String clientId) {
        ClientNodeDTO clientNodeDTO = clientSvc.getClientNodeDTO(clientId);
        for (FullNotice notice : noticesSvc.getNoticesByClientId(clientId)) {
            clientNodeDTO.getNotices().add(new NoticeNodeDTO(notice));
        }
        return clientNodeDTO;
    }

    @GetMapping("/api/graphs/clients/{clientId}/relationships")
    public String getClientsRelQuery(
            @RequestParam(value = "max", required = false, defaultValue = "25") String max,
            @PathVariable String clientId
    ) {
        Map<String, Object> bindParams = new HashMap<>();
        bindParams.put("limit", getLimit(max, 50));
        bindParams.put("clientId", clientId);
        Neo4jClient.RunnableSpecTightToDatabase response = neo4jClient.query(
                "MATCH (:Client {id: $clientId})-[:REL_PERSON]-(p:Person) " +
                        "MATCH (p)-[ref:REL_PERSON]-(c:Client) return c, ref, p LIMIT $limit")
                .bindAll(bindParams);
        try {
            return neo4jObjectMapper.writeValueAsString(response.fetch().all());
        } catch (JsonProcessingException e) {
            throw new FosBadRequestException();
        }
    }

    @PutMapping("/api/graphs/clients/{clientId}/relationships")
    public ClientNodeDTO addRelationship(
            @PathVariable String clientId,
            @RequestBody AddRelationshipDTO addRelationshipDTO,
            @RequestHeader("authToken") String authToken
    ) {
        authProvider.getUid(authToken);
        logger.debug("Adding relationship for client: {}", addRelationshipDTO);
        ClientNode client = clientSvc.getClientNode(clientId);
        client.getPersons().add(new ClientPersonLink(
                new PersonNode(addRelationshipDTO.getRelName()),
                addRelationshipDTO.getCoiType().toString(),
                addRelationshipDTO.getCoiSubtype().toString(),
                clientId, UUID.randomUUID().toString()
        ));
        clientSvc.save(client);
        return new ClientNodeDTO(client);
    }

    @PutMapping("/api/graphs/persons/{personId}/relationships")
    public PersonNodeDTO addRelationshipForPerson(
            @PathVariable String personId,
            @RequestBody AddRelationshipDTO addRelationshipDTO,
            @RequestHeader("authToken") String authToken
    ) {
        authProvider.getUid(authToken);
        logger.debug("Adding relationship for person: {}", addRelationshipDTO);
        OrganisationNode org = organisationsGraphRepo.findOrgHydratingPersons(addRelationshipDTO.getRelId()).orElseThrow();
        logger.debug("Found {}", org);

        PersonNode personNode = personsSvc.getPersonGraphObject(personId);

        // generate link first, then check if it exists ...
        PersonConflictLink conflictLink = new PersonConflictLink(
                personId, org,
                addRelationshipDTO.getCoiType().toString(), addRelationshipDTO.getCoiSubtype().toString(),
                UUID.randomUUID().toString()
        );

        if (!personNode.getConflicts().contains(conflictLink)) {
            personNode.getConflicts().add(conflictLink);
        }
        return new PersonNodeDTO(personsSvc.save(personNode));
    }

    @GetMapping("/api/graphs/notices/{noticeId}/metadata")
    public NoticeNodeDTO getNotice(@PathVariable String noticeId) {
        NoticeNodeDTO noticeNodeDTO = noticesSvc.getNoticeDTO(noticeId);
        for (AwardDTO awardDTO : awardsSvc.getAwardsForNotice(noticeId)) {
            noticeNodeDTO.addAward(awardDTO);
        }
        return noticeNodeDTO;
    }

    @GetMapping("/api/graphs/notices/{noticeId}/children")
    public String getNoticeChildrenQuery(
            @RequestParam(value = "max", required = false, defaultValue = "50") String max,
            @PathVariable String noticeId
    ) {
        Map<String, Object> bindParams = new HashMap<>();
        bindParams.put("limit", getLimit(max, 50));
        bindParams.put("noticeId", noticeId);
        Neo4jClient.RunnableSpecTightToDatabase response = neo4jClient.query(
                "MATCH (a:Award)-[ref]-(n:Notice) WHERE n.hidden=false AND n.id = $noticeId " +
                        "RETURN a, ref, n LIMIT $limit").bindAll(bindParams);
        try {
            return neo4jObjectMapper.writeValueAsString(response.fetch().all());
        } catch (JsonProcessingException e) {
            throw new FosBadRequestException();
        }
    }

    @GetMapping("/api/graphs/notices/{noticeId}/parents")
    public String getNoticeParentsQuery(
            @RequestParam(value = "max", required = false, defaultValue = "50") String max,
            @PathVariable String noticeId
    ) {
        Map<String, Object> bindParams = new HashMap<>();
        bindParams.put("limit", getLimit(max, 50));
        bindParams.put("noticeId", noticeId);
        Neo4jClient.RunnableSpecTightToDatabase response = neo4jClient.query(
                "MATCH (:Notice {id: $noticeId})-[:PUBLISHED]-(c:Client) " +
                        "MATCH (c)-[ref:PUBLISHED]-(n:Notice) return c, ref, n LIMIT $limit")
                .bindAll(bindParams);
        try {
            return neo4jObjectMapper.writeValueAsString(response.fetch().all());
        } catch (JsonProcessingException e) {
            throw new FosBadRequestException();
        }
    }

    @GetMapping("/api/graphs/organisations/{orgId}")
    public String getSpecificOrganisationForSearchResult(@PathVariable String orgId) {
        Map<String, Object> bindParams = new HashMap<>();
        bindParams.put("orgId", orgId);
        Neo4jClient.RunnableSpecTightToDatabase response = neo4jClient.query(
                "MATCH (o:Organisation) WHERE o.id = $orgId " +
                        "OPTIONAL MATCH (o:Organisation)-[ref:AWARDED_TO]-(a:Award) " +
                        "RETURN o, ref, a").bindAll(bindParams);
        try {
            return neo4jObjectMapper.writeValueAsString(response.fetch().all());
        } catch (JsonProcessingException e) {
            throw new FosBadRequestException();
        }
    }

    @GetMapping("/api/graphs/organisations/{orgId}/metadata")
    public OrganisationDTO getOrgMetadata(@PathVariable String orgId) {
        OrganisationNode organisationNode = organisationsGraphRepo.findOrgNotHydratingPersons(orgId).orElseThrow();
        return new OrganisationDTO(organisationNode);
    }

    @GetMapping("/api/graphs/organisations/{orgId}/relationships")
    public String getOrgRelsQuery(
            @RequestParam(value = "max", required = false, defaultValue = "25") String max,
            @PathVariable String orgId
    ) {
        Map<String, Object> bindParams = new HashMap<>();
        bindParams.put("limit", getLimit(max, 50));
        bindParams.put("orgId", orgId);
        // if the org is verified or has a LEGAL_ENTITY then it should be marked as 'resolved'
        Neo4jClient.RunnableSpecTightToDatabase response = neo4jClient.query(
                "MATCH (:Organisation {id: $orgId})-[:ORG_PERSON|CONFLICT|LEGAL_ENTITY]-(p) " +
                        "MATCH (p)-[ref:ORG_PERSON|CONFLICT|LEGAL_ENTITY]-(o:Organisation) " +
                        "WITH o, exists((o)-[:LEGAL_ENTITY]-()) as o_resolved, ref, p, exists((p)-[:LEGAL_ENTITY]-()) as p_resolved " +
                        "RETURN {neo4j_id: id(o), labels: labels(o), properties: o{.*, resolved: o_resolved}} as o, " +
                        "ref, {neo4j_id: id(p), labels: labels(p), properties: p{.*, resolved: p_resolved}} as p LIMIT $limit")
                .bindAll(bindParams);
        try {
            return neo4jObjectMapper.writeValueAsString(response.fetch().all());
        } catch (JsonProcessingException e) {
            throw new FosBadRequestException();
        }
    }

    @GetMapping("/api/graphs/organisations/{orgId}/notices")
    public List<String> getNoticesForOrg(
            @PathVariable String orgId
    ) {
        return awardsSvc.getAwardsForOrg(orgId).stream().map(AwardNode::getNoticeId).collect(Collectors.toList());
    }

    @GetMapping("/api/graphs/persons/{personId}/relationships")
    public String getPersonQuery(
            @RequestParam(value = "reqType", required = false, defaultValue = "org") String reqType,
            @RequestParam(value = "max", required = false, defaultValue = "50") String max,
            @PathVariable String personId
    ) {
        Map<String, Object> bindParams = new HashMap<>();
        bindParams.put("limit", getLimit(max, 100));
        bindParams.put("personId", personId);

        String reqStr = reqType.equals("org") ?
                "MATCH (p:Person {id: $personId})-[ref:ORG_PERSON|CONFLICT]-(o:Organisation) " +
                        "RETURN p, ref, o LIMIT $limit" :
                "MATCH (p:Person {id: $personId})-[ref:CONFLICT|REL_PERSON]-(c:Client) " +
                        "RETURN p, ref, c LIMIT $limit";

        Neo4jClient.RunnableSpecTightToDatabase response = neo4jClient.query(reqStr).bindAll(bindParams);
        try {
            return neo4jObjectMapper.writeValueAsString(response.fetch().all());
        } catch (JsonProcessingException e) {
            throw new FosBadRequestException();
        }
    }

    @GetMapping("/api/graphs/persons/{personId}/metadata")
    public PersonNodeDTO getPersonMetadata(
            @PathVariable String personId
    ) {
        List<OrganisationNode> links = personsSvc.getOrgPersonLinks(personId);
        PersonNode person = personsSvc.getPersonGraphObject(personId);
        return new PersonNodeDTO(person, links);
    }

    @PutMapping("/api/graphs/persons/{personId}/populate")
    public String populatePersonRecord(
            @PathVariable String personId,
            @RequestHeader("authToken") String authToken
    ) {
        authProvider.getUid(authToken);
        return "OK";
    }

    private int getLimit(String limit, int hardLimit) {
        int lim = Integer.parseInt(limit);
        return (lim > hardLimit) ? hardLimit : (lim > 0) ? lim : 1;
    }
}
