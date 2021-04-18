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
            "WITH {neo4j_id: id(node), labels: labels(node), properties: node{.*, details: [o.name], score: score}} as populated " +
            "RETURN populated, populated.properties.id AS id, populated.properties.details AS details, populated.properties.commonName AS name, populated.properties.score AS score")
    List<GraphFTSDetailedResponse> findAnyPersonsMatchingWithDetails(String query);

}
