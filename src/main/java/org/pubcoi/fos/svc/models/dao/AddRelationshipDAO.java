package org.pubcoi.fos.svc.models.dao;

public class AddRelationshipDAO {
    Boolean isNewObject;
    String relId;
    String relName;
    AddRelTypeEnum relType;
    AddRelCoiTypeEnum coiType;
    AddRelCoiSubtypeEnum coiSubtype;
    String comments;
    Boolean evidenceComments;
    Boolean evidenceFile;
    String evidenceURL;

    public String getRelName() {
        return relName;
    }

    public AddRelationshipDAO setRelName(String relName) {
        this.relName = relName;
        return this;
    }

    public AddRelCoiTypeEnum getCoiType() {
        return coiType;
    }

    public AddRelationshipDAO setCoiType(AddRelCoiTypeEnum coiType) {
        this.coiType = coiType;
        return this;
    }

    public AddRelCoiSubtypeEnum getCoiSubtype() {
        return coiSubtype;
    }

    public AddRelationshipDAO setCoiSubtype(AddRelCoiSubtypeEnum coiSubtype) {
        this.coiSubtype = coiSubtype;
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

    public String getRelId() {
        return relId;
    }

    public AddRelationshipDAO setRelId(String relId) {
        this.relId = relId;
        return this;
    }

    public AddRelTypeEnum getRelType() {
        return relType;
    }

    public AddRelationshipDAO setRelType(AddRelTypeEnum relType) {
        this.relType = relType;
        return this;
    }

    @Override
    public String toString() {
        return "AddRelationshipDAO{" +
                "relId='" + relId + '\'' +
                ", relName='" + relName + '\'' +
                ", relType=" + relType +
                ", coiType=" + coiType +
                ", coiSubtype=" + coiSubtype +
                ", comments='" + comments + '\'' +
                ", evidenceComments=" + evidenceComments +
                ", evidenceFile=" + evidenceFile +
                ", evidenceURL='" + evidenceURL + '\'' +
                '}';
    }

    public Boolean getNewObject() {
        return isNewObject;
    }

    public AddRelationshipDAO setNewObject(Boolean newObject) {
        isNewObject = newObject;
        return this;
    }
}
