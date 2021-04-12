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
import org.pubcoi.fos.svc.gdb.ClientNodeFTS;
import org.pubcoi.fos.svc.gdb.OrgNodeFTS;
import org.pubcoi.fos.svc.gdb.OrganisationsGraphRepo;
import org.pubcoi.fos.svc.gdb.PersonNodeFTS;
import org.pubcoi.fos.svc.models.dao.*;
import org.pubcoi.fos.svc.models.dao.fts.GenericIDNameFTSResponse;
import org.pubcoi.fos.svc.models.dao.neo.InternalNodeSerializer;
import org.pubcoi.fos.svc.models.dao.neo.InternalRelationshipSerializer;
import org.pubcoi.fos.svc.models.neo.nodes.AwardNode;
import org.pubcoi.fos.svc.models.neo.nodes.ClientNode;
import org.pubcoi.fos.svc.models.neo.nodes.OrganisationNode;
import org.pubcoi.fos.svc.models.neo.nodes.PersonNode;
import org.pubcoi.fos.svc.models.neo.relationships.ClientPersonLink;
import org.pubcoi.fos.svc.services.AwardsSvc;
import org.pubcoi.fos.svc.services.ClientsSvc;
import org.pubcoi.fos.svc.services.NoticesSvc;
import org.pubcoi.fos.svc.services.PersonsSvc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
public class GraphRest {
    private static final Logger logger = LoggerFactory.getLogger(GraphRest.class);

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

    {
        neo4jObjectMapper = new ObjectMapper();
        SimpleModule sm = new SimpleModule();
        sm.addSerializer(InternalNode.class, new InternalNodeSerializer());
        sm.addSerializer(InternalRelationship.class, new InternalRelationshipSerializer());
        neo4jObjectMapper.registerModule(sm);
        neo4jObjectMapper.registerModule(new JavaTimeModule()); // for ZonedDateTime
        neo4jObjectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); // DT not epoch please
    }

    public GraphRest(
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
    public List<GenericIDNameFTSResponse> findAnyPersonQuery(@RequestParam String query) {
        if (query.isEmpty()) return new ArrayList<>();
        return personNodeFTS.findAnyPersonsMatching(String.format("%s*", query))
                .stream()
                .map(GenericIDNameFTSResponse::new)
                .collect(Collectors.toList());
    }

    @GetMapping("/api/graphs/_search/organisations")
    public List<GenericIDNameFTSResponse> findAnyOrgQuery(@RequestParam String query) {
        if (query.isEmpty()) return new ArrayList<>();

        // open / wildcard
        Set<GenericIDNameFTSResponse> responses = orgNodeFTS.findAnyOrgsMatching(String.format("*%s*", query), 5).stream().
                map(r -> new GraphSearchResponseDAO(r, NodeTypeEnum.organisation))
                .collect(Collectors.toSet());

        // entire token
        responses.addAll(orgNodeFTS.findAnyOrgsMatching(query, 5).stream().
                map(r -> new GraphSearchResponseDAO(r, NodeTypeEnum.organisation))
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
     * Currently performs 4x searches and then returns the most relevant results ... so not terribly efficient
     *
     * @param query The search parameters
     * @return List of top responses, ordered by best -> worst match
     */
    @GetMapping("/api/graphs/_search")
    public List<GraphSearchResponseDAO> findAnyClientOrOrgQuery(@RequestParam String query) {
        if (query.isEmpty()) return new ArrayList<>();
        Set<GraphSearchResponseDAO> response = clientNodeFTS.findAnyClientsMatching(String.format("*%s*", query), 5)
                .stream()
                .map(r -> new GraphSearchResponseDAO(r, NodeTypeEnum.client))
                .collect(Collectors.toSet());

        response.addAll(clientNodeFTS.findAnyClientsMatching(query, 5)
                .stream()
                .map(r -> new GraphSearchResponseDAO(r, NodeTypeEnum.client))
                .collect(Collectors.toSet()));

        response.addAll(orgNodeFTS.findAnyOrgsMatching(String.format("*%s*", query), 5).stream().
                map(r -> new GraphSearchResponseDAO(r, NodeTypeEnum.organisation))
                .collect(Collectors.toSet())
        );

        response.addAll(orgNodeFTS.findAnyOrgsMatching(query, 5).stream().
                map(r -> new GraphSearchResponseDAO(r, NodeTypeEnum.organisation))
                .collect(Collectors.toSet())
        );

        return response.stream()
                .sorted(Comparator.comparing(GraphSearchResponseDAO::getScore).reversed())
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
                "CALL apoc.create.setProperty(n, \"has_awards\", exists((n)-[:AWARDS]-())) YIELD node " +
                "RETURN c, ref, n LIMIT $limit").bindAll(bindParams);
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
    public AwardDAO getAward(@PathVariable String awardId) {
        return awardsSvc.getAwardDetailsDAOWithAttachments(awardId);
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
                "CALL apoc.create.setProperty(n, \"has_awards\", exists((n)-[:AWARDS]-())) YIELD node " +
                "RETURN c, ref, n").bindAll(bindParams);
        try {
            return neo4jObjectMapper.writeValueAsString(response.fetch().all());
        } catch (JsonProcessingException e) {
            throw new FosBadRequestException();
        }
    }

    @GetMapping("/api/graphs/clients/{clientId}/metadata")
    public ClientNodeDAO getClient(@PathVariable String clientId) {
        ClientNodeDAO clientNodeDAO = clientSvc.getClientNodeDAO(clientId);
        for (FullNotice notice : noticesSvc.getNoticesByClientId(clientId)) {
            clientNodeDAO.getNotices().add(new NoticeNodeDAO(notice));
        }
        return clientNodeDAO;
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
    public ClientNodeDAO addRelationship(
            @PathVariable String clientId,
            @RequestBody AddRelationshipDAO addRelationshipDAO
    ) {
        logger.debug("Adding relationship {}", addRelationshipDAO);
        ClientNode client = clientSvc.getClientNode(clientId);
        client.getPersons().add(new ClientPersonLink(
                new PersonNode(addRelationshipDAO.getName()),
                addRelationshipDAO.getRelType().toString(),
                addRelationshipDAO.getRelSubtype().toString(),
                clientId, UUID.randomUUID().toString()
        ));
        clientSvc.save(client);
        return new ClientNodeDAO(client);
    }

    @GetMapping("/api/graphs/notices/{noticeId}/metadata")
    public NoticeNodeDAO getNotice(@PathVariable String noticeId) {
        NoticeNodeDAO noticeNodeDAO = noticesSvc.getNoticeDAO(noticeId);
        for (AwardDAO awardDAO : awardsSvc.getAwardsForNotice(noticeId)) {
            noticeNodeDAO.addAward(awardDAO);
        }
        return noticeNodeDAO;
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
        Neo4jClient.RunnableSpecTightToDatabase response = neo4jClient.query("match (o:Organisation)-[ref:AWARDED_TO]-(a:Award) " +
                "WHERE o.id = $orgId " +
                "RETURN a, ref, o").bindAll(bindParams);
        try {
            return neo4jObjectMapper.writeValueAsString(response.fetch().all());
        } catch (JsonProcessingException e) {
            throw new FosBadRequestException();
        }
    }

    @GetMapping("/api/graphs/organisations/{orgId}/metadata")
    public OrganisationDAO getOrgMetadata(@PathVariable String orgId) {
        OrganisationNode organisationNode = organisationsGraphRepo.findOrgNotHydratingPersons(orgId).orElseThrow();
        return new OrganisationDAO(organisationNode);
    }

    @GetMapping("/api/graphs/organisations/{orgId}/relationships")
    public String getOrgRelsQuery(
            @RequestParam(value = "max", required = false, defaultValue = "25") String max,
            @PathVariable String orgId
    ) {
        Map<String, Object> bindParams = new HashMap<>();
        bindParams.put("limit", getLimit(max, 50));
        bindParams.put("orgId", orgId);
        Neo4jClient.RunnableSpecTightToDatabase response = neo4jClient.query(
                "MATCH (:Organisation {id: $orgId})-[:ORG_PERSON]-(p:Person) " +
                        "MATCH (p)-[ref:ORG_PERSON]-(o:Organisation) return o, ref, p LIMIT $limit")
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
            @RequestParam(value = "max", required = false, defaultValue = "50") String max,
            @PathVariable String personId
    ) {
        Map<String, Object> bindParams = new HashMap<>();
        bindParams.put("limit", getLimit(max, 100));
        bindParams.put("personId", personId);
        Neo4jClient.RunnableSpecTightToDatabase response = neo4jClient.query(
                "MATCH (p:Person {id: $personId})-[ref:ORG_PERSON]-(o:Organisation) " +
                        "return p, ref, o LIMIT $limit")
                .bindAll(bindParams);
        try {
            return neo4jObjectMapper.writeValueAsString(response.fetch().all());
        } catch (JsonProcessingException e) {
            throw new FosBadRequestException();
        }
    }

    @GetMapping("/api/graphs/persons/{personId}/metadata")
    public PersonDAO getPersonMetadata(
            @PathVariable String personId
    ) {
        List<OrganisationNode> links = personsSvc.getOrgPersonLinks(personId);
        PersonNode person = personsSvc.getPersonGraphObject(personId);
        return new PersonDAO(person, links);
    }

    private int getLimit(String limit, int hardLimit) {
        int lim = Integer.parseInt(limit);
        return (lim > hardLimit) ? hardLimit : (lim > 0) ? lim : 1;
    }
}
