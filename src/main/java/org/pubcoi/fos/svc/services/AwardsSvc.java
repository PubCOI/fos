package org.pubcoi.fos.svc.services;

import org.pubcoi.fos.svc.models.core.CFAward;

public interface AwardsSvc {

    void addAward(CFAward cfAward, String currentUser);
}
