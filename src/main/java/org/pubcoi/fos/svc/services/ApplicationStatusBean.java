package org.pubcoi.fos.svc.services;

public class ApplicationStatusBean {

    Boolean batch;
    Boolean debug;

    public Boolean getBatch() {
        return batch;
    }

    public ApplicationStatusBean setBatch(Boolean batch) {
        this.batch = batch;
        return this;
    }

    public Boolean getDebug() {
        return debug;
    }

    public ApplicationStatusBean setDebug(Boolean debug) {
        this.debug = debug;
        return this;
    }
}
