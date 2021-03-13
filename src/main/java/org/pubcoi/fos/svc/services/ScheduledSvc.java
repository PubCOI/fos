package org.pubcoi.fos.svc.services;

public interface ScheduledSvc {
    void populateFosOrgsMDBFromAwards();

    void populateOCCompaniesFromFosOrgs();
}
