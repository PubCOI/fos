package org.pubcoi.fos.models.neo.relationships;

import org.pubcoi.fos.models.neo.nodes.OrganisationNode;
import org.springframework.data.neo4j.core.schema.RelationshipProperties;
import org.springframework.data.neo4j.core.schema.TargetNode;

import java.time.ZonedDateTime;

@RelationshipProperties
public class AwardOrgLink {

    @TargetNode
    OrganisationNode organisationNode;

    ZonedDateTime awardedDate;
    ZonedDateTime startDate;
    ZonedDateTime endDate;

    public AwardOrgLink(){}

    public AwardOrgLink(OrganisationNode organisationNode) {
        this.setOrganisationNode(organisationNode);
    }

    public OrganisationNode getOrganisationNode() {
        return organisationNode;
    }

    public AwardOrgLink setOrganisationNode(OrganisationNode organisationNode) {
        this.organisationNode = organisationNode;
        return this;
    }

    public ZonedDateTime getAwardedDate() {
        return awardedDate;
    }

    public AwardOrgLink setAwardedDate(ZonedDateTime awardedDate) {
        this.awardedDate = awardedDate;
        return this;
    }

    public ZonedDateTime getStartDate() {
        return startDate;
    }

    public AwardOrgLink setStartDate(ZonedDateTime startDate) {
        this.startDate = startDate;
        return this;
    }

    public ZonedDateTime getEndDate() {
        return endDate;
    }

    public AwardOrgLink setEndDate(ZonedDateTime endDate) {
        this.endDate = endDate;
        return this;
    }
}
