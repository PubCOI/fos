package org.pubcoi.fos.models.neo.nodes;

import org.pubcoi.fos.models.core.DataSources;
import org.pubcoi.fos.models.neo.relationships.OrgLELink;
import org.springframework.data.neo4j.core.schema.DynamicLabels;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.HashSet;
import java.util.Set;

@Node(primaryLabel = "organisation")
public class Organisation {

    @Id
    String id;

    String companyName;
    String companyAddress;
    DataSources source;

    Boolean verified;

    @DynamicLabels
    Set<String> labels = new HashSet<>();

    @Relationship("LEGAL_ENTITY")
    OrgLELink legalEntity;

    public String getCompanyName() {
        return companyName;
    }

    public Organisation setCompanyName(String companyName) {
        this.companyName = companyName;
        return this;
    }

    public String getCompanyAddress() {
        return companyAddress;
    }

    public Organisation setCompanyAddress(String companyAddress) {
        this.companyAddress = companyAddress;
        return this;
    }

    public DataSources getSource() {
        return source;
    }

    public Organisation setSource(DataSources source) {
        this.source = source;
        return this;
    }

    public String getId() {
        return id;
    }

    public Organisation setId(String id) {
        this.id = id;
        return this;
    }

    @Override
    public String toString() {
        return "Organisation{" +
                "id='" + id + '\'' +
                '}';
    }

    public OrgLELink getLegalEntity() {
        return legalEntity;
    }

    public Organisation setLegalEntity(OrgLELink legalEntity) {
        this.legalEntity = legalEntity;
        return this;
    }

    public Set<String> getLabels() {
        return labels;
    }

    public Organisation setLabels(Set<String> labels) {
        this.labels = labels;
        return this;
    }

    public boolean isVerified() {
        return verified;
    }

    public Organisation setVerified(Boolean verified) {
        this.verified = verified;
        if (verified) {
            labels.add("verified");
        }
        else {
            labels.remove("verified");
        }
        return this;
    }
}
