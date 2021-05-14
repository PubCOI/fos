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

import org.pubcoi.fos.svc.models.neo.nodes.ClientNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;

import java.util.List;
import java.util.Optional;

public interface ClientsGraphRepo extends Neo4jRepository<ClientNode, Long> {
    Logger logger = LoggerFactory.getLogger(ClientsGraphRepo.class);

    Optional<ClientNode> findByFosId(String fosId);

    @Query("MATCH paths = (c:Client {fosId: $clientId})-[rel]->(n:Notice) " +
            "RETURN c, collect(rel) AS PUBLISHED, collect(n) AS NOTICES")
    Optional<ClientNode> findClientHydratingNotices(String clientId);

    @Query("RETURN exists((:Client {fosId: $clientId})-[:PUBLISHED]-(:Notice {fosId: $noticeId}))")
    boolean relationshipExists(String clientId, String noticeId);

    @Query("MATCH (c:Client) OPTIONAL MATCH (c)-[rel:PUBLISHED]-(n:Notice) " +
            "RETURN c, collect(rel), collect(n) AS NOTICES")
    List<ClientNode> getClientsWithRels();
}
