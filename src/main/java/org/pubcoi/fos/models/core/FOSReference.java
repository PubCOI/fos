package org.pubcoi.fos.models.core;

public class FOSReference {
    public String getFosID() {
        return fosID;
    }

    public FOSReference setFosID(type type, String fosID) {
        this.refType = type;
        this.fosID = fosID;
        return this;
    }

    type refType;

    public enum type {fos_organisation}

    String fosID;
}
