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

package org.pubcoi.fos.svc.repos.gdb;


import org.pubcoi.fos.svc.models.neo.nodes.AwardNode;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;

import java.util.List;

public interface AwardsGraphRepo extends Neo4jRepository<AwardNode, String> {

    @Query("MATCH (a:Award)--(o:Organisation) " +
            "WHERE o.id = $orgId " +
            "RETURN count (a)")
    Long countAwardsToSupplier(String orgId);

    @Query("MATCH (a:Award)-[rel:AWARDED_TO]-(o:Organisation) " +
            "WHERE o.id = $orgId " +
            "RETURN a"
    )
    List<AwardNode> getAwardsForSupplier(String orgId);
}
