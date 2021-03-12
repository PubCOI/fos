package org.pubcoi.fos.svc.services;

import org.pubcoi.fos.svc.mdb.AwardsMDBRepo;
import org.pubcoi.fos.svc.models.core.CFAward;
import org.pubcoi.fos.svc.models.dao.AwardDAO;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

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

    @Override
    public Set<AwardDAO> getAwardsForNotice(String noticeID) {
        return awardsMDBRepo.findAllByNoticeId(noticeID).stream().map(AwardDAO::new).collect(Collectors.toSet());
    }
}
