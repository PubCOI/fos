package org.pubcoi.fos.svc.gdb;

import org.pubcoi.fos.svc.exceptions.FosRuntimeException;
import org.pubcoi.fos.svc.models.neo.nodes.ClientNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;

import java.util.Optional;

public interface ClientsGraphRepo extends Neo4jRepository<ClientNode, String> {
    Logger logger = LoggerFactory.getLogger(ClientsGraphRepo.class);

    @Query("MATCH(c:Client) WHERE c.id = $clientId RETURN c")
    Optional<ClientNode> findByIdEquals(String clientId);

    @Query("MATCH paths = (c:Client)-[rel]->(n:Notice) " +
            "WHERE c.id = $clientId " +
            "RETURN c, collect(rel) AS AWARDED, collect(n) AS NOTICES")
    Optional<ClientNode> findClientHydratingNotices(String clientId);

    default Optional<ClientNode> findById(String id) {
        logger.error("Don't run this query, it goes and executes a [:*] and merrily blows itself up");
        throw new FosRuntimeException("NOOP");
    }

}
