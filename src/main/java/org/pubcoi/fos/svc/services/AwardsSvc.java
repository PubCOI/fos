package org.pubcoi.fos.svc.services;

import org.pubcoi.fos.svc.models.core.CFAward;
import org.pubcoi.fos.svc.models.dao.AwardDAO;
import org.pubcoi.fos.svc.models.neo.nodes.AwardNode;

import java.util.List;
import java.util.Set;

public interface AwardsSvc {

    void addAward(CFAward cfAward, String currentUser);

    Set<AwardDAO> getAwardsForNotice(String noticeID);

    AwardDAO getAwardDetailsDAOWithAttachments(String awardId);

    List<AwardNode> getAwardsForOrg(String orgId);
}
