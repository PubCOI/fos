package org.pubcoi.fos.svc.models.neo.nodes;

import com.opencorporates.schemas.OCCompanySchema;
import org.pubcoi.fos.svc.models.core.FosOrganisation;
import org.pubcoi.fos.svc.models.core.NodeReference;
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
    String jurisdiction;
    String reference;
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

    public OrganisationNode(FosOrganisation org) {
        this.id = org.getId();
        this.jurisdiction = org.getJurisdiction();
        this.reference = org.getReference();
        this.name = org.getCompanyName();
        this.verified = org.getVerified();
    }

    public OrganisationNode(OCCompanySchema ocCompany) {
        this.id = String.format("%s:%s", ocCompany.getJurisdictionCode(), ocCompany.getCompanyNumber());
        this.jurisdiction = ocCompany.getJurisdictionCode();
        this.reference = ocCompany.getCompanyNumber();
        this.name = ocCompany.getName();
        this.verified = true;
    }

    public OrganisationNode(NodeReference target) {
        this.id = target.getId();
    }

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

    public String getJurisdiction() {
        return jurisdiction;
    }

    public OrganisationNode setJurisdiction(String jurisdiction) {
        this.jurisdiction = jurisdiction;
        return this;
    }

    public String getReference() {
        return reference;
    }

    public OrganisationNode setReference(String reference) {
        this.reference = reference;
        return this;
    }
}
