package org.pubcoi.fos.svc.gdb;

import org.pubcoi.fos.svc.models.neo.nodes.NoticeNode;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;

import java.util.List;

public interface NoticesGraphRepo extends Neo4jRepository<NoticeNode, String> {

    @Query("MATCH (n:Notice)-[rel:PUBLISHED]-(c:Client) " +
            "WHERE c.id = $clientId " +
            "RETURN n")
    List<NoticeNode> findAllNoticesByClientNode(String clientId);
}
