package org.pubcoi.fos.gdb;

import org.pubcoi.fos.models.neo.nodes.NoticeNode;
import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface NoticesGRepo extends Neo4jRepository<NoticeNode, String> {
}
