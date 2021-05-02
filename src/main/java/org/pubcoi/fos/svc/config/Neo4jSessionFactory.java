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

import org.neo4j.ogm.config.Configuration;
import org.neo4j.ogm.session.Session;
import org.neo4j.ogm.session.SessionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class Neo4jSessionFactory {


    @Value("${spring.neo4j.uri}")
    String neo4jUri;

    @Value("${spring.neo4j.authentication.username}")
    String neo4jUsername;

    @Value("${spring.neo4j.authentication.password}")
    String neo4jPassword;

    private SessionFactory sessionFactory;

    private Neo4jSessionFactory() {
    }

    @PostConstruct
    public void postConstruct() {
        sessionFactory = new SessionFactory(new Configuration.Builder()
                .uri(neo4jUri)
                .credentials(neo4jUsername, neo4jPassword)
                .build(), "org.pubcoi.fos.svc.models.neo.nodes", "org.pubcoi.fos.svc.models.neo.relationships");
    }

    public Session getNeo4jSession() {
        return sessionFactory.openSession();
    }

}
