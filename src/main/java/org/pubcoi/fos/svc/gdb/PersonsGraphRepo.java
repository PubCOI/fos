package org.pubcoi.fos.svc.gdb;

import org.pubcoi.fos.svc.models.neo.nodes.PersonNode;
import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface PersonsGraphRepo extends Neo4jRepository<PersonNode, String> {

    boolean existsByOcId(String ocid);

}
