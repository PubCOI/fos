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

import org.pubcoi.fos.svc.models.neo.nodes.AwardNode;
import org.pubcoi.fos.svc.models.queries.AwardsGraphResponse;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface AwardsListRepo extends CrudRepository<AwardNode, Long> {

    @Query("MATCH (c:Client)-[:PUBLISHED]-(:Notice)-[:AWARDED]-(award:Award)-[awardOrgLink:AWARDED_TO]-(awardee:Organisation) " +
            "OPTIONAL MATCH (awardee)-[orgOrgLink:LEGAL_ENTITY]-(legalEntity:Organisation {verified: true}) " +
            "RETURN c.name AS clientName, award AS awardNode, awardOrgLink, awardee, orgOrgLink, legalEntity")
    List<AwardsGraphResponse> getAwardsWithRels();

}
