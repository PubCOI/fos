package org.pubcoi.fos.svc.gdb;

import org.pubcoi.fos.svc.models.neo.nodes.OrganisationNode;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;

import java.util.Optional;

public interface OrganisationsGraphRepo extends Neo4jRepository<OrganisationNode, String> {

    @Query("MATCH paths = (o:Organisation)-[rel]->(p:Person) " +
            "WHERE o.id = $orgId " +
            "RETURN o, collect(rel) AS ORG_PERSON, collect(p) AS orgPersons")
    Optional<OrganisationNode> findOrgHydratingPersons(String orgId);

    @Query("MATCH (o:Organisation {id: $orgId}) return o")
    Optional<OrganisationNode> findOrgNotHydratingPersons(String orgId);

}
