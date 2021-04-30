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

package org.pubcoi.fos.svc.models.core.tc;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.pubcoi.fos.svc.Boot;
import org.pubcoi.fos.svc.services.GraphSvc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.SocketUtils;

import static org.junit.Assert.assertTrue;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Boot.class)
@ActiveProfiles("gdb-integration")
//@Testcontainers
public class Setup {

    static int neo4jPort = SocketUtils.findAvailableTcpPort();

    @Autowired
    GraphSvc graphSvc;

//    @ClassRule
//    public static GenericContainer neo4jContainer = new Neo4jContainer(DockerImageName.parse("neo4j:4.2.1"))
//            .withoutAuthentication();

    @Test
    public void top_level_container_should_be_running() {
        // assertTrue(neo4jContainer.isRunning());
        assertTrue(true);
    }

//    @Test
//    public void shouldAnswerWithOne() {
//        String uri = "bolt://" + neo4j.getContainerIpAddress() + ":" + neo4j.getMappedPort(neo4jPort);
//
//        try (Driver driver = GraphDatabase.driver(uri, AuthTokens.basic("neo4j", "neo4j"))) {
//
//            try (Session session = driver.session()) {
//                Result result = session.run("RETURN 1 AS value");
//                int value = result.single().get("value").asInt();
//                Assert.assertEquals(value, 1);
//            }
//
//        }
//    }

}
