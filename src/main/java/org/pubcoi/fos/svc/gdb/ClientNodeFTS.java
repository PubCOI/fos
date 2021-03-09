package org.pubcoi.fos.svc.gdb;

import org.pubcoi.fos.svc.models.neo.nodes.ClientNode;
import org.pubcoi.fos.svc.models.queries.ClientNodeFTSResponse;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ClientNodeFTS extends CrudRepository<ClientNode, String> {
    @Query("CALL db.index.fulltext.queryNodes(\"clients-fts\", $query) " +
            "YIELD node, score " +
            "WHERE node.canonical=true " +
            "RETURN node, node.id, node.clientName, score")
    List<ClientNodeFTSResponse> findAllDTOProjectionsWithCustomQuery(String query);
}
