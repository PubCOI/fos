/*
 * Copyright (c) 2021 PubCOI.org. This file is part of Fos@PubCOI.
 *
 * Fos@PubCOI is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Fos@PubCOI is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Fos@PubCOI.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.pubcoi.fos.svc.repos.gdb.custom;

import org.pubcoi.fos.svc.models.neo.nodes.PersonNode;
import org.pubcoi.fos.svc.models.queries.GraphFTSDetailedResponse;
import org.pubcoi.fos.svc.models.queries.GraphFTSResponse;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface PersonNodeFTS extends CrudRepository<PersonNode, Long> {

    @Query("CALL db.index.fulltext.queryNodes(\"persons-fts\", $query) " +
            "YIELD node, score " +
            "RETURN node, node.fosId, node.commonName AS name, score"
    )
    List<GraphFTSResponse> findAnyPersonsMatching(String query);

    // returns persons including the company/companies they're associated with
    @Query("CALL db.index.fulltext.queryNodes(\"persons-fts\", $query) " +
            "YIELD node, score " +
            "MATCH(p:Person {fosId: node.fosId})-[rel:ORG_PERSON]-(o:Organisation) " +
            "WITH {neo4j_id: id(node), labels: labels(node), properties: node{.*, details: [o.name], score: score}} as populated " +
            "RETURN populated, populated.properties.fosId AS fosId, populated.properties.details AS details, populated.properties.commonName AS name, populated.properties.score AS score")
    List<GraphFTSDetailedResponse> findAnyPersonsMatchingWithDetails(String query);

}
