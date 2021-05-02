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

import org.pubcoi.fos.svc.models.neo.nodes.NoticeNode;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;

import java.util.List;
import java.util.Optional;

public interface NoticesGraphRepo extends Neo4jRepository<NoticeNode, Long> {

    @Query("MATCH (n:Notice)-[rel:PUBLISHED]-(c:Client {fosId: $clientId}) " +
            "RETURN n")
    List<NoticeNode> findAllNoticesByClientNode(String clientId);

    Optional<NoticeNode> findByFosId(String noticeId);

    @Query("RETURN exists((:Notice {fosId: $noticeId})-[:AWARDED]-(:Award {fosId: $awardId}))")
    boolean isLinkedToAward(String noticeId, String awardId);
}
