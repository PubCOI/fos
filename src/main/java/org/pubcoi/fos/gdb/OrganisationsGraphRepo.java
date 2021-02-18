package org.pubcoi.fos.gdb;

import org.pubcoi.fos.models.neo.nodes.Organisation;
import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface OrganisationsGraphRepo extends Neo4jRepository<Organisation, String> {
}
