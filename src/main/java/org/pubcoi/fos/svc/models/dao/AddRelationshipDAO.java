package org.pubcoi.fos.svc.models.dao;

public class AddRelationshipDAO {
    String name;
    AddRelationshipRelTypeEnum relType;
    AddRelationshipRelSubtypeEnum relSubtype;
    String comments;
    Boolean evidenceComments;
    Boolean evidenceFile;
    String evidenceURL;

    public String getName() {
        return name;
    }

    public AddRelationshipDAO setName(String name) {
        this.name = name;
        return this;
    }

    public AddRelationshipRelTypeEnum getRelType() {
        return relType;
    }

    public AddRelationshipDAO setRelType(AddRelationshipRelTypeEnum relType) {
        this.relType = relType;
        return this;
    }

    public AddRelationshipRelSubtypeEnum getRelSubtype() {
        return relSubtype;
    }

    public AddRelationshipDAO setRelSubtype(AddRelationshipRelSubtypeEnum relSubtype) {
        this.relSubtype = relSubtype;
        return this;
    }

    public String getComments() {
        return comments;
    }

    public AddRelationshipDAO setComments(String comments) {
        this.comments = comments;
        return this;
    }

    public Boolean getEvidenceComments() {
        return evidenceComments;
    }

    public AddRelationshipDAO setEvidenceComments(Boolean evidenceComments) {
        this.evidenceComments = evidenceComments;
        return this;
    }

    public Boolean getEvidenceFile() {
        return evidenceFile;
    }

    public AddRelationshipDAO setEvidenceFile(Boolean evidenceFile) {
        this.evidenceFile = evidenceFile;
        return this;
    }

    public String getEvidenceURL() {
        return evidenceURL;
    }

    public AddRelationshipDAO setEvidenceURL(String evidenceURL) {
        this.evidenceURL = evidenceURL;
        return this;
    }

    @Override
    public String toString() {
        return "AddRelationshipDAO{" +
                "name='" + name + '\'' +
                ", relType=" + relType +
                ", relSubtype=" + relSubtype +
                ", comments='" + comments + '\'' +
                ", evidenceComments=" + evidenceComments +
                ", evidenceFile=" + evidenceFile +
                ", evidenceURL='" + evidenceURL + '\'' +
                '}';
    }
}
