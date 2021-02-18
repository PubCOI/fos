package org.pubcoi.fos.gdb;


import org.pubcoi.fos.models.neo.nodes.AwardNode;
import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface AwardsGraphRepo extends Neo4jRepository<AwardNode, String> {

}
