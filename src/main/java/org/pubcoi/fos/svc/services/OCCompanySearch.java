package org.pubcoi.fos.svc.services;

import org.pubcoi.fos.svc.models.oc.OCWrapper;

public interface OCCompanySearch {
    OCWrapper doSearch(String query);
}
