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

package org.pubcoi.fos.svc.repos.gdb.jpa;


import org.pubcoi.fos.svc.exceptions.FosRuntimeException;
import org.pubcoi.fos.svc.models.neo.nodes.AwardNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;

import java.util.List;
import java.util.Optional;

public interface AwardsGraphRepo extends Neo4jRepository<AwardNode, Long> {
    Logger logger = LoggerFactory.getLogger(AwardsGraphRepo.class);

    @Query("MATCH (a:Award)--(o:Organisation {fosId: $orgId}) " +
            "RETURN count (a)")
    Long countAwardsToSupplier(String orgId);

    @Query("MATCH (a:Award)-[rel:AWARDED_TO]-(o:Organisation {fosId: $orgId}) RETURN a")
    List<AwardNode> getAwardsForSupplier(String orgId);

    @Query("MATCH (a:Award) RETURN a")
    List<AwardNode> findAllNotHydrating();

    @Query("RETURN exists((:Award {fosId: $awardId})-[:AWARDED_TO]-(:Organisation {fosId: $orgId}))")
    boolean relationshipExists(String awardId, String orgId);

    default List<AwardNode> findAll() {
        logger.error("Don't run this query, will cause runaway transaction");
        throw new FosRuntimeException("NOOP");
    }

    @Query("MATCH(a:Award {fosId: $awardId}) RETURN a")
    Optional<AwardNode> findByFosIdNotHydratingAwardees(String awardId);

    @Query("MATCH paths = (a:Award {fosId: $awardId})-[rel]->(o:Organisation) " +
            "RETURN a, collect(rel) AS AWARDED_TO, collect(o) AS AWARDEES")
    Optional<AwardNode> findByFosIdHydratingAwardees(String awardId);
}
