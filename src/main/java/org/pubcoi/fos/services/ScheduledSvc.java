package org.pubcoi.fos.services;

public interface ScheduledSvc {
    void populateFOSOrgsMDBFromAwards();

    void populateOCCompaniesFromFOSOrgs();
}
