package org.pubcoi.fos.models.neo.relationships;

import org.pubcoi.fos.models.neo.nodes.OrganisationNode;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.RelationshipProperties;
import org.springframework.data.neo4j.core.schema.TargetNode;

@RelationshipProperties
public class OrgLELink {

    @Id
    String id;

    @TargetNode
    OrganisationNode organisationNode;

    public OrgLELink() {}

    public OrgLELink(String id, OrganisationNode organisationNode)  {
        this.id = id;
        this.organisationNode = organisationNode;
    }

}
