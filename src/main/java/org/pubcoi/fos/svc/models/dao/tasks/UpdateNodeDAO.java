package org.pubcoi.fos.svc.models.dao.tasks;

/*
 Used when returning the ClientNode and OrganisationNode tasks to the user after transaction execution
 */
public class UpdateNodeDAO {

    String response;

    public String getResponse() {
        return response;
    }

    public UpdateNodeDAO setResponse(String response) {
        this.response = response;
        return this;
    }
}
