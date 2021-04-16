package org.pubcoi.fos.svc.services;

import org.pubcoi.fos.svc.models.core.JurisdictionEnum;
import org.pubcoi.fos.svc.models.oc.OCWrapper;

public interface OCRestSvc {
    OCWrapper doCompanySearch(String query);

    OCWrapper getCompany(String query, JurisdictionEnum jurisdiction);
}
