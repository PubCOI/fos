package org.pubcoi.fos.models.neo.nodes;

import org.pubcoi.fos.models.neo.relationships.AwardOrgLink;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.annotation.Version;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.time.ZonedDateTime;

@Node(primaryLabel = "contract_award")
public class AwardNode {
    private static final Logger logger = LoggerFactory.getLogger(AwardNode.class);

    @Relationship("AWARDED_TO")
    AwardOrgLink organisation;

    @Id
    String id;
    Long value;
    @Version
    Long version = 1L;

    public OrganisationNode getOrganisation() {
        return (null != organisation) ? organisation.getOrganisationNode() : null;
    }

    public AwardNode setOrganisation(OrganisationNode organisationNode, ZonedDateTime awardedDate, ZonedDateTime startDate, ZonedDateTime endDate) {
        if (null == this.organisation) {
            this.organisation = new AwardOrgLink(organisationNode).setAwardedDate(awardedDate).setStartDate(startDate).setEndDate(endDate);
        }
        else {
            logger.warn("REMOVING relationship between entities {} {}", this, organisationNode);
            this.organisation.setOrganisationNode(organisationNode);
        }
        return this;
    }

    public String getId() {
        return id;
    }

    public AwardNode setId(String id) {
        this.id = id;
        return this;
    }

    public Long getValue() {
        return value;
    }

    public AwardNode setValue(Long value) {
        this.value = value;
        return this;
    }

    public Long getVersion() {
        return version;
    }

    @Override
    public String toString() {
        return "AwardNode{" +
                "id='" + id + '\'' +
                '}';
    }
}
