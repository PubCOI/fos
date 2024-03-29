/*
 * Copyright (c) 2021 PubCOI.org. This file is part of Fos@PubCOI.
 *
 * Fos@PubCOI is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Fos@PubCOI is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Fos@PubCOI.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.pubcoi.fos.svc.models.dto;

public class AddRelationshipDTO {
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

    public AddRelationshipDTO setRelName(String relName) {
        this.relName = relName;
        return this;
    }

    public AddRelCoiTypeEnum getCoiType() {
        return coiType;
    }

    public AddRelationshipDTO setCoiType(AddRelCoiTypeEnum coiType) {
        this.coiType = coiType;
        return this;
    }

    public AddRelCoiSubtypeEnum getCoiSubtype() {
        return coiSubtype;
    }

    public AddRelationshipDTO setCoiSubtype(AddRelCoiSubtypeEnum coiSubtype) {
        this.coiSubtype = coiSubtype;
        return this;
    }

    public String getComments() {
        return comments;
    }

    public AddRelationshipDTO setComments(String comments) {
        this.comments = comments;
        return this;
    }

    public Boolean getEvidenceComments() {
        return evidenceComments;
    }

    public AddRelationshipDTO setEvidenceComments(Boolean evidenceComments) {
        this.evidenceComments = evidenceComments;
        return this;
    }

    public Boolean getEvidenceFile() {
        return evidenceFile;
    }

    public AddRelationshipDTO setEvidenceFile(Boolean evidenceFile) {
        this.evidenceFile = evidenceFile;
        return this;
    }

    public String getEvidenceURL() {
        return evidenceURL;
    }

    public AddRelationshipDTO setEvidenceURL(String evidenceURL) {
        this.evidenceURL = evidenceURL;
        return this;
    }

    public String getRelId() {
        return relId;
    }

    public AddRelationshipDTO setRelId(String relId) {
        this.relId = relId;
        return this;
    }

    public AddRelTypeEnum getRelType() {
        return relType;
    }

    public AddRelationshipDTO setRelType(AddRelTypeEnum relType) {
        this.relType = relType;
        return this;
    }

    @Override
    public String toString() {
        return "AddRelationshipDTO{" +
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

    public AddRelationshipDTO setNewObject(Boolean newObject) {
        isNewObject = newObject;
        return this;
    }
}
