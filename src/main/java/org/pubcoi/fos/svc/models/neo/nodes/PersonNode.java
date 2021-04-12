package org.pubcoi.fos.svc.models.neo.nodes;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Node(primaryLabel = "Person")
public class PersonNode implements FosEntity {

    // if the person is a director, they will have an OCID and a UUID
    // if not, they will just have a UUID
    // a user CANNOT be assigned as an office bearer unless they have an OCID
    // todo: allow records to be merged

    @Id
    String id;
    Boolean hidden = false;
    String ocId;
    String commonName;
    String occupation;
    String nationality;
    Set<String> transactions = new HashSet<>();

    PersonNode() {}

    public PersonNode(String openCorporatesId, String name, String occupation, String nationality, String transactionId) {
        this.id = DigestUtils.sha1Hex(String.format("oc:%s", openCorporatesId));
        this.ocId = openCorporatesId;
        this.commonName = name;
        this.occupation = occupation;
        this.nationality = nationality;
        this.transactions.add(transactionId);
    }

    public PersonNode(String name) {
        this.id = UUID.randomUUID().toString();
        this.commonName = name;
    }

    public String getId() {
        return id;
    }

    @Override
    public Boolean getHidden() {
        return hidden;
    }

    @Override
    public FosEntity setHidden(Boolean hidden) {
        this.hidden = hidden;
        return this;
    }

    public PersonNode setId(String id) {
        this.id = id;
        return this;
    }

    public String getOcId() {
        return ocId;
    }

    public PersonNode setOcId(String ocId) {
        this.ocId = ocId;
        return this;
    }

    public String getCommonName() {
        return commonName;
    }

    public PersonNode setCommonName(String commonName) {
        this.commonName = commonName;
        return this;
    }

    public String getOccupation() {
        return occupation;
    }

    public PersonNode setOccupation(String occupation) {
        this.occupation = occupation;
        return this;
    }

    public String getNationality() {
        return nationality;
    }

    public PersonNode setNationality(String nationality) {
        this.nationality = nationality;
        return this;
    }

    public Set<String> getTransactions() {
        return transactions;
    }

    public PersonNode setTransactions(Set<String> transactions) {
        this.transactions = transactions;
        return this;
    }
}