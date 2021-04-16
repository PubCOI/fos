package org.pubcoi.fos.svc.gdb;

import org.pubcoi.fos.svc.models.neo.nodes.PersonNode;
import org.pubcoi.fos.svc.models.queries.GraphFTSDetailedResponse;
import org.pubcoi.fos.svc.models.queries.GraphFTSResponse;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface PersonNodeFTS extends CrudRepository<PersonNode, String> {

    @Query("CALL db.index.fulltext.queryNodes(\"persons-fts\", $query) " +
            "YIELD node, score " +
            "RETURN node, node.id, node.commonName AS name, score"
    )
    List<GraphFTSResponse> findAnyPersonsMatching(String query);

    // returns persons including the company/companies they're associated with
    @Query("CALL db.index.fulltext.queryNodes(\"persons-fts\", $query) " +
            "YIELD node, score " +
            "MATCH(p:Person {id: node.id})-[rel:ORG_PERSON]-(o:Organisation) " +
            "CALL apoc.create.setProperty(node, \"details\", [o.name]) " +
            "YIELD node AS populated " +
            "RETURN populated, populated.id, populated.details AS details, node.commonName AS name, score")
    List<GraphFTSDetailedResponse> findAnyPersonsMatchingWithDetails(String query);

}
