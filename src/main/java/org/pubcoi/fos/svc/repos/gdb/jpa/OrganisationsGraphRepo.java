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
import org.pubcoi.fos.svc.models.neo.nodes.OrganisationNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;

import java.util.List;
import java.util.Optional;

public interface OrganisationsGraphRepo extends Neo4jRepository<OrganisationNode, Long> {
    Logger logger = LoggerFactory.getLogger(OrganisationsGraphRepo.class);

    @Query("MATCH (o:Organisation {fosId: $orgId}) " +
            "OPTIONAL MATCH (o:Organisation)-[rel:ORG_PERSON]->(p:Person) " +
            "RETURN o, collect(rel) AS ORG_PERSON, collect(p) AS orgPersons")
    Optional<OrganisationNode> findByFosIdHydratingPersons(String orgId);

    @Query("MATCH (p:Person {fosId: $personId})-[rel:ORG_PERSON]-(o:Organisation) " +
            "RETURN p, rel AS ORG_PERSON, o AS ORGANISATION")
    List<OrganisationNode> findAllByPerson(String personId);

    @Query("RETURN exists((:Organisation {jurisdiction: $jurisdiction, reference: $reference}))")
    boolean existsByJurisdictionAndReference(String jurisdiction, String reference);

    @Query("MATCH (o:Organisation) return o")
    List<OrganisationNode> findAllNotHydrating();

    @Query("MATCH (o:Organisation {fosId: $fosId}) RETURN o")
    Optional<OrganisationNode> findByFosId(String fosId);

    @Query("RETURN exists((:Organisation {fosId: $orgId})-[:ORG_PERSON]-(:Person {fosId: $personId}))")
    boolean relationshipExists(String orgId, String personId);

    default List<OrganisationNode> findAll() {
        logger.error("Don't run this query, will cause runaway transaction");
        throw new FosRuntimeException("NOOP");
    }
}
