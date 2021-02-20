package org.pubcoi.fos.gdb;

import org.pubcoi.fos.models.neo.nodes.ClientNode;
import org.pubcoi.fos.models.queries.ClientNodeFTSResponse;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ClientNodeFTS extends CrudRepository<ClientNode, String> {
    @Query("CALL db.index.fulltext.queryNodes(\"clients-fts\", $query) " +
            "YIELD node, score RETURN node, node.id, node.clientName, score")
    List<ClientNodeFTSResponse> findAllDTOProjectionsWithCustomQuery(String query);
}
