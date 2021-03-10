package org.pubcoi.fos.svc.services;

import org.pubcoi.fos.svc.mdb.AwardsMDBRepo;
import org.pubcoi.fos.svc.models.core.CFAward;
import org.springframework.stereotype.Service;

@Service
public class AwardsSvcImpl implements AwardsSvc {

    final AwardsMDBRepo awardsMDBRepo;

    public AwardsSvcImpl(AwardsMDBRepo awardsMDBRepo) {
        this.awardsMDBRepo = awardsMDBRepo;
    }

    @Override
    public void addAward(CFAward cfAward, String currentUser) {
        awardsMDBRepo.save(cfAward);
    }
}
