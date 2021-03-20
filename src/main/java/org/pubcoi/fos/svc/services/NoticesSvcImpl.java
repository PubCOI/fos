package org.pubcoi.fos.svc.services;

import org.pubcoi.fos.models.cf.AwardDetailParentType;
import org.pubcoi.fos.models.cf.FullNotice;
import org.pubcoi.fos.svc.exceptions.ItemNotFoundException;
import org.pubcoi.fos.svc.gdb.NoticesGraphRepo;
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
    final NoticesGraphRepo noticesGRepo;
    final AwardsSvc awardsSvc;

    public NoticesSvcImpl(
            NoticesMDBRepo noticesMDBRepo,
            NoticesGraphRepo noticesGRepo,
            AwardsSvc awardsSvc
    ) {
        this.noticesMDBRepo = noticesMDBRepo;
        this.noticesGRepo = noticesGRepo;
        this.awardsSvc = awardsSvc;
    }

    @Override
    public void addNotice(FullNotice notice, String currentUser) {
        // remove data we don't want / need
        notice.getNotice().setContactDetails(null);

        noticesMDBRepo.save(notice);
        for (AwardDetailParentType.AwardDetail awardDetail : notice.getAwards().getAwardDetail()) {
            awardsSvc.addAward(new CFAward(notice, awardDetail), currentUser);
        }
    }

    @Override
    public NoticeNodeDAO getNoticeDAO(String noticeId) {
        return new NoticeNodeDAO(noticesMDBRepo.findById(noticeId).orElseThrow(ItemNotFoundException::new));
    }

    @Override
    public List<FullNotice> getNoticesByClientId(String clientId) {
        return noticesGRepo.findAllNoticesByClientNode(clientId).stream()
                .map(n -> noticesMDBRepo.findById(n.getId()).orElseThrow())
                .sorted(Comparator.comparing(FullNotice::getCreatedDate))
                .collect(Collectors.toList());
    }
}
