package org.pubcoi.fos.svc.repos.gdb;

import org.pubcoi.fos.svc.models.neo.nodes.OrganisationNode;
import org.pubcoi.fos.svc.models.queries.GraphFTSResponse;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface OrgNodeFTS extends CrudRepository<OrganisationNode, String> {

    @Query("CALL db.index.fulltext.queryNodes(\"orgs-fts\", $query) " +
            "YIELD node, score " +
            "RETURN node, node.id, node.name, score " +
            "LIMIT $limit"
    )
    List<GraphFTSResponse> findAnyOrgsMatching(String query, Integer limit);

}
