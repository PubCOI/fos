package org.pubcoi.fos.svc.services;

import org.pubcoi.fos.models.cf.FullNotice;
import org.pubcoi.fos.svc.models.dao.NoticeNodeDAO;

public interface NoticesSvc {
    void addNotice(FullNotice notice, String currentUser);

    NoticeNodeDAO getNotice(String noticeID);
}
