package org.pubcoi.fos.svc.services;

import org.pubcoi.fos.svc.models.core.CFAward;
import org.pubcoi.fos.svc.models.dao.AwardDAO;

import java.util.Set;

public interface AwardsSvc {

    void addAward(CFAward cfAward, String currentUser);

    Set<AwardDAO> getAwardsForNotice(String noticeID);
}
