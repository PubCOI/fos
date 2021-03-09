package org.pubcoi.fos.svc.services;

public interface ScheduledSvc {
    void populateFOSOrgsMDBFromAwards();

    void populateOCCompaniesFromFOSOrgs();
}
