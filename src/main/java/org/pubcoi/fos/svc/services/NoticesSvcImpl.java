package org.pubcoi.fos.svc.services;

import org.pubcoi.fos.models.cf.AwardDetailParentType;
import org.pubcoi.fos.models.cf.FullNotice;
import org.pubcoi.fos.svc.exceptions.ItemNotFoundException;
import org.pubcoi.fos.svc.gdb.NoticesGRepo;
import org.pubcoi.fos.svc.mdb.NoticesMDBRepo;
import org.pubcoi.fos.svc.models.core.CFAward;
import org.pubcoi.fos.svc.models.dao.NoticeNodeDAO;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NoticesSvcImpl implements NoticesSvc {

    final NoticesMDBRepo noticesMDBRepo;
    final NoticesGRepo noticesGRepo;
    final AwardsSvc awardsSvc;

    public NoticesSvcImpl(
            NoticesMDBRepo noticesMDBRepo,
            NoticesGRepo noticesGRepo,
            AwardsSvc awardsSvc
    ) {
        this.noticesMDBRepo = noticesMDBRepo;
        this.noticesGRepo = noticesGRepo;
        this.awardsSvc = awardsSvc;
    }

    @Override
    public void addNotice(FullNotice notice, String currentUser) {
        noticesMDBRepo.save(notice);
        for (AwardDetailParentType.AwardDetail awardDetail : notice.getAwards().getAwardDetail()) {
            awardsSvc.addAward(new CFAward(notice, awardDetail), currentUser);
        }
    }

    @Override
    public NoticeNodeDAO getNoticeDAO(String noticeID) {
        return new NoticeNodeDAO(noticesMDBRepo.findById(noticeID).orElseThrow(ItemNotFoundException::new));
    }

    @Override
    public List<FullNotice> getNotices(String clientID) {
        return noticesGRepo.findAllNoticesByClientNode(clientID).stream()
                .map(n -> noticesMDBRepo.findById(n.getId()).orElseThrow())
                .sorted(Comparator.comparing(FullNotice::getCreatedDate))
                .collect(Collectors.toList());
    }
}
