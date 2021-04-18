package org.pubcoi.fos.svc.repos.gdb;

import org.pubcoi.fos.svc.models.neo.nodes.ClientNode;
import org.pubcoi.fos.svc.models.queries.GraphFTSResponse;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ClientNodeFTS extends CrudRepository<ClientNode, String> {
    @Query("CALL db.index.fulltext.queryNodes(\"clients-fts\", $query) " +
            "YIELD node, score " +
            "WHERE node.canonical=true " +
            "RETURN node, node.id, node.name, score")
    List<GraphFTSResponse> findAllCanonicalClientNodesMatching(String query);

    @Query("CALL db.index.fulltext.queryNodes(\"clients-fts\", $query) " +
            "YIELD node, score " +
            "RETURN node, node.id, node.name, score " +
            "LIMIT $limit")
    List<GraphFTSResponse> findAnyClientsMatching(String query, Integer limit);
}
