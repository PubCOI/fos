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
import org.pubcoi.fos.svc.models.dao.*;
import org.pubcoi.fos.svc.models.dao.neo.InternalNodeSerializer;
import org.pubcoi.fos.svc.models.dao.neo.InternalRelationshipSerializer;
import org.pubcoi.fos.svc.services.AwardsSvc;
import org.pubcoi.fos.svc.services.ClientsSvc;
import org.pubcoi.fos.svc.services.NoticesSvc;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.stream.Collectors;

@RestController
public class GraphRest {

    final Neo4jClient neo4jClient;
    final ClientNodeFTS clientNodeFTS;
    final OrgNodeFTS orgNodeFTS;
    final ClientsSvc clientSvc;
    final NoticesSvc noticesSvc;
    final AwardsSvc awardsSvc;
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
            OrgNodeFTS orgNodeFTS, ClientsSvc clientSvc,
            NoticesSvc noticesSvc,
            AwardsSvc awardsSvc
    ) {
        this.neo4jClient = neo4jClient;
        this.clientNodeFTS = clientNodeFTS;
        this.orgNodeFTS = orgNodeFTS;
        this.clientSvc = clientSvc;
        this.noticesSvc = noticesSvc;
        this.awardsSvc = awardsSvc;
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
        return clientNodeFTS.findAllCanonicalClientNodesMatching(query)
                .stream()
                .filter(c -> !c.getId().equals(currentNode))
                .limit(5)
                .map(ClientNodeFTSDAOResponse::new)
                .collect(Collectors.toList());
    }

    /**
     * Return client or org nodes that match the current search
     *
     * Currently performs 4x searches and then returns the most relevant results ... so not terribly efficient
     *
     * @param query The search parameters
     * @return List of top responses, ordered by best -> worst match
     */
    @GetMapping("/api/ui/graphs/search")
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

    /**
     * Used when a user clicks on a search result on the graph
     * @param clientId the client id to return details on
     * @return a neo4j object that can be read by cytoscape
     */
    @GetMapping("/api/ui/graph/clients/{clientId}")
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

    @GetMapping("/api/ui/graph/organisations/{orgId}")
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

    @GetMapping("/api/ui/clients/{clientID}")
    public ClientNodeDAO getClient(@PathVariable String clientID) {
        ClientNodeDAO clientNodeDAO = clientSvc.getClientNode(clientID);
        for (FullNotice notice : noticesSvc.getNoticesByClientId(clientID)) {
            clientNodeDAO.getNotices().add(new NoticeNodeDAO(notice));
        }
        return clientNodeDAO;
    }

    @GetMapping("/api/ui/notices/{noticeID}")
    public NoticeNodeDAO getNotice(@PathVariable String noticeID) {
        NoticeNodeDAO noticeNodeDAO = noticesSvc.getNoticeDAO(noticeID);
        for (AwardDAO awardDAO : awardsSvc.getAwardsForNotice(noticeID)) {
            noticeNodeDAO.addAward(awardDAO);
        }
        return noticeNodeDAO;
    }

    @GetMapping("/api/ui/queries/initial")
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

    @GetMapping("/api/ui/queries/notices/{noticeId}/children")
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

    @GetMapping("/api/ui/queries/awards/{awardId}/children")
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

    @GetMapping("/api/ui/queries/awards/{awardId}/parents")
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

    @GetMapping("/api/ui/queries/notices/{noticeId}/parents")
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

    private int getLimit(String limit, int hardLimit) {
        int lim = Integer.parseInt(limit);
        return (lim > hardLimit) ? hardLimit : (lim > 0) ? lim : 1;
    }
}
