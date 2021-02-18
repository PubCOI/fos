package org.pubcoi.fos.models.neo.relationships;

import org.pubcoi.fos.models.neo.nodes.Organisation;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.RelationshipProperties;
import org.springframework.data.neo4j.core.schema.TargetNode;

@RelationshipProperties
public class OrgLELink {

    @Id
    String id;

    @TargetNode
    Organisation organisation;

    public OrgLELink() {}

    public OrgLELink(String id, Organisation organisation)  {
        this.id = id;
        this.organisation = organisation;
    }

}
