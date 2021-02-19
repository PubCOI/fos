package org.pubcoi.fos.gdb;

import org.pubcoi.fos.models.neo.nodes.ClientNode;
import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface ClientsGraphRepo extends Neo4jRepository<ClientNode, String> {
}