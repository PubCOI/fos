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

import org.pubcoi.fos.svc.models.neo.nodes.OrganisationNode;
import org.pubcoi.fos.svc.models.queries.GraphFTSResponse;
import org.pubcoi.fos.svc.models.queries.OrganisationsGraphListResponse;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface OrganisationsGraphCustomRepo extends CrudRepository<OrganisationNode, Long> {

    @Query("CALL db.index.fulltext.queryNodes(\"orgs-fts\", $query) " +
            "YIELD node, score " +
            "RETURN node, node.fosId, node.name, score " +
            "LIMIT $limit"
    )
    List<GraphFTSResponse> findAnyOrgsMatching(String query, Integer limit);

    @Query("MATCH (o:Organisation) OPTIONAL MATCH (o)-[rel:AWARDED_TO]-(a:Award) RETURN o AS organisation, collect(a.fosId) AS awards")
        // todo note that orgs with AKA references will be counted separately from their parent entity at least for now
    List<OrganisationsGraphListResponse> findOrgsWithAwardIds();

}
