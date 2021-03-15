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
            logger.info("Ran create index {}", rs);
        }
        // This is REALLY nasty ...
        // Josh Bloch would wince (and quote rule #69)
        // I can't seem to find a way to call up on legacy indices and check if it exists beforehand:
        // For now we are checking to see if this is thrown...
        catch (InvalidDataAccessResourceUsageException e) {
            logger.info("Index appears to already exist");
        }
    }

}
