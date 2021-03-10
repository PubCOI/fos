package org.pubcoi.fos.svc.services;

import org.pubcoi.fos.models.cf.AwardDetailParentType;
import org.pubcoi.fos.models.cf.FullNotice;
import org.pubcoi.fos.svc.mdb.NoticesMDBRepo;
import org.pubcoi.fos.svc.models.core.CFAward;
import org.springframework.stereotype.Service;

@Service
public class NoticesSvcImpl implements NoticesSvc {

    final NoticesMDBRepo noticesMDBRepo;
    final AwardsSvc awardsSvc;

    public NoticesSvcImpl(NoticesMDBRepo noticesMDBRepo, AwardsSvc awardsSvc) {
        this.noticesMDBRepo = noticesMDBRepo;
        this.awardsSvc = awardsSvc;
    }

    @Override
    public void addNotice(FullNotice notice, String currentUser) {
        noticesMDBRepo.save(notice);
        for (AwardDetailParentType.AwardDetail awardDetail : notice.getAwards().getAwardDetail()) {
            awardsSvc.addAward(new CFAward(notice, awardDetail), currentUser);
        }
    }
}
