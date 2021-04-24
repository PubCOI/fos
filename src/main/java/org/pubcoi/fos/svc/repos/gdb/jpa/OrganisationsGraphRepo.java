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

import org.pubcoi.fos.svc.models.neo.nodes.OrganisationNode;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;

import java.util.List;
import java.util.Optional;

public interface OrganisationsGraphRepo extends Neo4jRepository<OrganisationNode, String> {

    @Query("MATCH paths = (o:Organisation)-[rel]->(p:Person) " +
            "WHERE o.id = $orgId " +
            "RETURN o, collect(rel) AS ORG_PERSON, collect(p) AS orgPersons")
    Optional<OrganisationNode> findOrgHydratingPersons(String orgId);

    @Query("MATCH (o:Organisation {id: $orgId}) return o")
    Optional<OrganisationNode> findOrgNotHydratingPersons(String orgId);

    @Query("MATCH (p:Person {id: $personId})-[rel:ORG_PERSON]-(o:Organisation) " +
            "RETURN p, rel AS ORG_PERSON, o AS ORGANISATION")
    List<OrganisationNode> findAllByPerson(String personId);

    boolean existsByJurisdictionAndReference(String jurisdiction, String reference);

}
