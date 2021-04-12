package org.pubcoi.fos.svc.services;

import org.pubcoi.cdm.cf.FullNotice;
import org.pubcoi.fos.svc.gdb.AwardsGraphRepo;
import org.pubcoi.fos.svc.mdb.AwardsMDBRepo;
import org.pubcoi.fos.svc.mdb.NoticesMDBRepo;
import org.pubcoi.fos.svc.models.core.CFAward;
import org.pubcoi.fos.svc.models.dao.AttachmentDAO;
import org.pubcoi.fos.svc.models.dao.AwardDAO;
import org.pubcoi.fos.svc.models.neo.nodes.AwardNode;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AwardsSvcImpl implements AwardsSvc {

    final AwardsMDBRepo awardsMDBRepo;
    final AttachmentSvc attachmentSvc;
    final AwardsGraphRepo awardsGraphRepo;
    final NoticesMDBRepo noticesMDBRepo;

    public AwardsSvcImpl(
            AwardsMDBRepo awardsMDBRepo,
            AttachmentSvc attachmentSvc,
            AwardsGraphRepo awardsGraphRepo,
            NoticesMDBRepo noticesMDBRepo) {
        this.awardsMDBRepo = awardsMDBRepo;
        this.attachmentSvc = attachmentSvc;
        this.awardsGraphRepo = awardsGraphRepo;
        this.noticesMDBRepo = noticesMDBRepo;
    }

    @Override
    public void addAward(CFAward cfAward, String currentUser) {
        awardsMDBRepo.save(cfAward);
    }

    @Override
    public Set<AwardDAO> getAwardsForNotice(String noticeID) {
        return awardsMDBRepo.findAllByNoticeId(noticeID).stream().map(AwardDAO::new).collect(Collectors.toSet());
    }

    @Override
    public AwardDAO getAwardDetailsDAOWithAttachments(String awardId) {
        final CFAward award = awardsMDBRepo.findById(awardId).orElseThrow();
        AwardDAO awardDAO = new AwardDAO(award);
        List<AttachmentDAO> attachments = attachmentSvc.findAttachmentsByNoticeId(award.getNoticeId());
        if (null != award.getFosOrganisation() && null != award.getFosOrganisation().getId()) {
            awardDAO.setSupplierNumTotalAwards(awardsGraphRepo.countAwardsToSupplier(
                    award.getFosOrganisation().getId()
            ));
        }
        FullNotice notice = noticesMDBRepo.findById(award.getNoticeId()).orElseThrow();
        awardDAO.setNoticeTitle(notice.getNotice().getTitle());
        return awardDAO.setAttachments(attachments);
    }

    @Override
    public List<AwardNode> getAwardsForOrg(String orgId) {
        return awardsGraphRepo.getAwardsForSupplier(orgId);
    }
}
