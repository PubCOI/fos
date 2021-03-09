package org.pubcoi.fos.svc.gdb;

import org.pubcoi.fos.svc.models.neo.nodes.OrganisationNode;
import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface OrganisationsGraphRepo extends Neo4jRepository<OrganisationNode, String> {
}
