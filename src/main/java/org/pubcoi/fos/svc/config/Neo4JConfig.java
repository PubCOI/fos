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

package org.pubcoi.fos.svc.config;

import org.neo4j.driver.Driver;
import org.neo4j.driver.summary.ResultSummary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.InvalidDataAccessResourceUsageException;
import org.springframework.data.neo4j.core.Neo4jClient;

import javax.annotation.PostConstruct;

@Configuration
public class Neo4JConfig {
    private static final Logger logger = LoggerFactory.getLogger(Neo4JConfig.class);

    final Driver neo4jDriver;
    final Neo4jClient client;

    public Neo4JConfig(Driver neo4jDriver) {
        this.neo4jDriver = neo4jDriver;
        this.client = Neo4jClient.create(neo4jDriver);
    }

    @PostConstruct
    public void postConstruct() {
        try {
            ResultSummary rs = client.query(
                    "CALL db.index.fulltext.createNodeIndex(\"clients-fts\", [\"Client\"], [\"name\", \"postCode\"], {analyzer: \"english\"})"
            ).run();
            logger.info("Ran create node index {}", rs);
        }
        // This is REALLY nasty ...
        // Josh Bloch would wince (and quote rule #69)
        // I can't seem to find a way to call up on legacy indices and check if it exists beforehand:
        // For now we are checking to see if this is thrown...
        catch (InvalidDataAccessResourceUsageException e) {
            logger.info("Clients index appears to already exist");
        }

        // same again for the orgs index
        try {
            ResultSummary rs = client.query(
                    "CALL db.index.fulltext.createNodeIndex(\"orgs-fts\", [\"Organisation\"], [\"name\", \"id\"], {analyzer: \"english\"})"
            ).run();
            logger.info("Ran create orgs index {}", rs);
        } catch (InvalidDataAccessResourceUsageException e) {
            logger.info("Orgs index appears to already exist");
        }

        // and for persons
        try {
            // CALL db.index.fulltext.createNodeIndex("persons-fts", ["Person"], ["commonName"], {analyzer: "english"})
            ResultSummary rs = client.query(
                    "CALL db.index.fulltext.createNodeIndex(\"persons-fts\", [\"Person\"], [\"commonName\"], {analyzer: \"english\"})"
            ).run();
            logger.info("Ran create persons index {}", rs);
        } catch (InvalidDataAccessResourceUsageException e) {
            logger.info("Persons index appears to already exist");
        }
    }

}
