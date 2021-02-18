package org.pubcoi.fos.models.neo.nodes;

import org.pubcoi.fos.models.neo.relationships.OrgLELink;
import org.springframework.data.neo4j.core.schema.DynamicLabels;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.HashSet;
import java.util.Set;

@Node(primaryLabel = "organisation")
public class OrganisationNode {

    @Id
    String id;
    String companyName;
    Boolean verified;

    @DynamicLabels
    Set<String> labels = new HashSet<>();

    @Relationship("LEGAL_ENTITY")
    OrgLELink legalEntity;

    public String getCompanyName() {
        return companyName;
    }

    public OrganisationNode setCompanyName(String companyName) {
        this.companyName = companyName;
        return this;
    }

    public String getId() {
        return id;
    }

    public OrganisationNode setId(String id) {
        this.id = id;
        return this;
    }

    @Override
    public String toString() {
        return "OrganisationNode{" +
                "id='" + id + '\'' +
                '}';
    }

    public OrgLELink getLegalEntity() {
        return legalEntity;
    }

    public OrganisationNode setLegalEntity(OrgLELink legalEntity) {
        this.legalEntity = legalEntity;
        return this;
    }

    public Set<String> getLabels() {
        return labels;
    }

    OrganisationNode setLabels(Set<String> labels) {
        this.labels = labels;
        return this;
    }

    public boolean isVerified() {
        return verified;
    }

    public OrganisationNode setVerified(Boolean verified) {
        this.verified = verified;
        if (verified) {
            labels.add("verified");
        } else {
            labels.remove("verified");
        }
        return this;
    }
}
