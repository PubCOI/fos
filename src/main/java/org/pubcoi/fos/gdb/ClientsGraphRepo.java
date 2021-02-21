package org.pubcoi.fos.gdb;

import org.pubcoi.fos.exceptions.FOSRuntimeException;
import org.pubcoi.fos.models.neo.nodes.ClientNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;

import java.util.Optional;

public interface ClientsGraphRepo extends Neo4jRepository<ClientNode, String> {
    Logger logger = LoggerFactory.getLogger(ClientsGraphRepo.class);

    @Query("MATCH(n:Client) WHERE n.id = $clientID RETURN n")
    Optional<ClientNode> findByIdEquals(String clientID);

    @Query("MATCH(n:Client)-[*0..1]-(a) WHERE n.id = $clientID RETURN n")
    Optional<ClientNode> findByIdEqualsIncludeNeighbours(String clientID);

    @Query("MATCH paths = (n:Client)-[rel]->(t:Tender) where n.id = $clientID return n, collect(rel) AS PUBLISHED, collect(t) AS TENDERS")
    Optional<ClientNode> findByClientIdIncludeNeighboursIgnoringHidden(String clientID);

    default Optional<ClientNode> findById(String id) {
        throw new FOSRuntimeException("NOOP");
    }

}
