package org.pubcoi.fos.gdb;

import org.pubcoi.fos.models.neo.nodes.OrganisationNode;
import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface OrganisationsGraphRepo extends Neo4jRepository<OrganisationNode, String> {
}
