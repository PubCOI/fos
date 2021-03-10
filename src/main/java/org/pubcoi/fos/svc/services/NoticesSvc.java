package org.pubcoi.fos.svc.services;

import org.pubcoi.fos.models.cf.FullNotice;

public interface NoticesSvc {
    void addNotice(FullNotice notice, String currentUser);
}
