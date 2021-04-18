package org.pubcoi.fos.svc.gdb;

import org.pubcoi.fos.svc.models.neo.nodes.DeclaredInterest;
import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface DeclaredInterestRepo extends Neo4jRepository<DeclaredInterest, String> {
}
