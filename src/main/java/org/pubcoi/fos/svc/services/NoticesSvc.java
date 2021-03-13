package org.pubcoi.fos.svc.services;

import org.pubcoi.fos.models.cf.FullNotice;
import org.pubcoi.fos.svc.models.dao.NoticeNodeDAO;

import java.util.List;

public interface NoticesSvc {
    void addNotice(FullNotice notice, String currentUser);

    NoticeNodeDAO getNoticeDAO(String noticeID);

    List<FullNotice> getNotices(String clientID);
}
