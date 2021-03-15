package org.pubcoi.fos.svc.gdb;


import org.pubcoi.fos.svc.models.neo.nodes.AwardNode;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;

public interface AwardsGraphRepo extends Neo4jRepository<AwardNode, String> {

    @Query("MATCH (a:Award)--(o:Organisation) " +
            "WHERE o.id = $orgId " +
            "RETURN count (a)")
    Long countAwardsToSupplier(String orgId);
}
