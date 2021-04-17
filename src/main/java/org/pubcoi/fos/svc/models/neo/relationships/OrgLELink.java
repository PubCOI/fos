package org.pubcoi.fos.svc.models.neo.relationships;

import org.apache.commons.codec.digest.DigestUtils;
import org.pubcoi.fos.svc.models.neo.nodes.OrganisationNode;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.RelationshipProperties;
import org.springframework.data.neo4j.core.schema.TargetNode;

import java.util.ArrayList;
import java.util.List;

@RelationshipProperties
public class OrgLELink {

    @Id
    String id;

    @TargetNode
    OrganisationNode organisation;

    List<String> transactions = new ArrayList<>();

    Boolean hidden = false;

    public OrgLELink() {}

    public OrgLELink(OrganisationNode source, OrganisationNode target, String transactionId)  {
        this.id = DigestUtils.sha1Hex(String.format("%s:%s", source.getId(), target.getId()));
        this.organisation = target;
        this.transactions.add(transactionId);
    }

    public Boolean getHidden() {
        return hidden;
    }

    public OrgLELink setHidden(Boolean hidden) {
        this.hidden = hidden;
        return this;
    }

    public String getId() {
        return id;
    }

    public OrgLELink setId(String id) {
        this.id = id;
        return this;
    }

    public OrganisationNode getOrganisation() {
        return organisation;
    }

    public OrgLELink setOrganisation(OrganisationNode organisation) {
        this.organisation = organisation;
        return this;
    }

    public List<String> getTransactions() {
        return transactions;
    }

    public OrgLELink setTransactions(List<String> transactions) {
        this.transactions = transactions;
        return this;
    }
}
