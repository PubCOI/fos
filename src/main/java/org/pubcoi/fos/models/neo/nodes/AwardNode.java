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

    public Organisation getOrganisation() {
        return (null != organisation) ? organisation.getOrganisation() : null;
    }

    public AwardNode setOrganisation(Organisation organisation, ZonedDateTime awardedDate, ZonedDateTime startDate, ZonedDateTime endDate) {
        if (null == this.organisation) {
            this.organisation = new AwardOrgLink(organisation).setAwardedDate(awardedDate).setStartDate(startDate).setEndDate(endDate);
        }
        else {
            logger.warn("REMOVING relationship between entities {} {}", this, organisation);
            this.organisation.setOrganisation(organisation);
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

    public AwardNode setVersion(Long version) {
        this.version = version;
        return this;
    }

    @Override
    public String toString() {
        return "AwardNode{" +
                "id='" + id + '\'' +
                '}';
    }
}
