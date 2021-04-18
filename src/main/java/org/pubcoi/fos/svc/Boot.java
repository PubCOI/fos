package org.pubcoi.fos.svc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;

@SpringBootApplication
@EnableMongoRepositories(basePackages = "org.pubcoi.fos.svc.repos.mdb")
@EnableNeo4jRepositories(basePackages = "org.pubcoi.fos.svc.repos.gdb")
public class Boot {
    public static void main(String[] args) {
        SpringApplication.run(Boot.class);
    }
}
