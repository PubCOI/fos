package org.pubcoi.fos.svc.gdb;


import org.pubcoi.fos.svc.models.neo.nodes.AwardNode;
import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface AwardsGraphRepo extends Neo4jRepository<AwardNode, String> {

}
