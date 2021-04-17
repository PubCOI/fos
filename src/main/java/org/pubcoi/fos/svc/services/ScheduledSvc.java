package org.pubcoi.fos.svc.services;

import com.opencorporates.schemas.OCCompanySchema;
import org.pubcoi.fos.svc.models.core.JurisdictionEnum;

public interface ScheduledSvc {
    void populateFosOrgsMDBFromAwards();

    OCCompanySchema getCompany(String companyReference, JurisdictionEnum jurisdiction);

    OCCompanySchema getCompany(String objectId);
}
