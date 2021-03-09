package org.pubcoi.fos.svc.models.neo.relationships;

import org.pubcoi.fos.svc.models.neo.nodes.OrganisationNode;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.RelationshipProperties;
import org.springframework.data.neo4j.core.schema.TargetNode;

@RelationshipProperties
public class OrgLELink {

    @Id
    String id;

    @TargetNode
    OrganisationNode organisation;

    Boolean hidden = false;

    public OrgLELink() {}

    public OrgLELink(String id, OrganisationNode organisation)  {
        this.id = id;
        this.organisation = organisation;
    }

    public Boolean getHidden() {
        return hidden;
    }

    public OrgLELink setHidden(Boolean hidden) {
        this.hidden = hidden;
        return this;
    }
}
