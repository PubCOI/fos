package org.pubcoi.fos.services;

import org.pubcoi.fos.models.cf.FullNotice;

public interface ScheduledSvc {
    void insertOrUpdateAwardCompany(FullNotice notice);

    void populateOne();
}
