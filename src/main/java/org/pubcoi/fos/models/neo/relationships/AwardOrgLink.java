package org.pubcoi.fos.models.neo.relationships;

import org.pubcoi.fos.models.neo.nodes.Organisation;
import org.springframework.data.neo4j.core.schema.RelationshipProperties;
import org.springframework.data.neo4j.core.schema.TargetNode;

import java.time.ZonedDateTime;

@RelationshipProperties
public class AwardOrgLink {

    @TargetNode
    Organisation organisation;

    ZonedDateTime awardedDate;
    ZonedDateTime startDate;
    ZonedDateTime endDate;

    public AwardOrgLink(){}

    public AwardOrgLink(Organisation organisation) {
        this.setOrganisation(organisation);
    }

    public Organisation getOrganisation() {
        return organisation;
    }

    public AwardOrgLink setOrganisation(Organisation organisation) {
        this.organisation = organisation;
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
