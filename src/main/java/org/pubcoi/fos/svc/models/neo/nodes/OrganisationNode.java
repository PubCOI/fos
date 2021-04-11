package org.pubcoi.fos.svc.models.neo.nodes;

import org.pubcoi.fos.svc.models.neo.relationships.OrgLELink;
import org.pubcoi.fos.svc.models.neo.relationships.OrgPersonLink;
import org.springframework.data.neo4j.core.schema.DynamicLabels;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Node(primaryLabel = "Organisation")
public class OrganisationNode implements FosEntity {

    @Id
    String id;
    String name;
    Boolean verified;
    Boolean hidden = false;

    @DynamicLabels
    Set<String> labels = new HashSet<>();

    @Relationship("LEGAL_ENTITY")
    OrgLELink legalEntity;

    @Relationship("ORG_PERSON")
    List<OrgPersonLink> orgPersons = new ArrayList<>();

    public OrganisationNode() {}

    public String getName() {
        return name;
    }

    public OrganisationNode setName(String name) {
        this.name = name;
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
            labels.add("Verified");
        } else {
            labels.remove("Verified");
        }
        return this;
    }

    @Override
    public Boolean getHidden() {
        return hidden;
    }

    @Override
    public OrganisationNode setHidden(Boolean hidden) {
        this.hidden = hidden;
        return this;
    }

    public Boolean getVerified() {
        return verified;
    }

    public List<OrgPersonLink> getOrgPersons() {
        return orgPersons;
    }

    public OrganisationNode setOrgPersons(List<OrgPersonLink> orgPersons) {
        this.orgPersons = orgPersons;
        return this;
    }
}
