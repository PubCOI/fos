package org.pubcoi.fos.svc.repos.gdb;


import org.pubcoi.fos.svc.models.neo.nodes.AwardNode;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;

import java.util.List;

public interface AwardsGraphRepo extends Neo4jRepository<AwardNode, String> {

    @Query("MATCH (a:Award)--(o:Organisation) " +
            "WHERE o.id = $orgId " +
            "RETURN count (a)")
    Long countAwardsToSupplier(String orgId);

    @Query("MATCH (a:Award)-[rel:AWARDED_TO]-(o:Organisation) " +
            "WHERE o.id = $orgId " +
            "RETURN a"
    )
    List<AwardNode> getAwardsForSupplier(String orgId);
}
