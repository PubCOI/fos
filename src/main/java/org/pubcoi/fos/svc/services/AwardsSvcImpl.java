package org.pubcoi.fos.svc.services;

import org.pubcoi.fos.svc.mdb.AwardsMDBRepo;
import org.pubcoi.fos.svc.models.core.CFAward;
import org.pubcoi.fos.svc.models.dao.AttachmentDAO;
import org.pubcoi.fos.svc.models.dao.AwardDAO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AwardsSvcImpl implements AwardsSvc {

    final AwardsMDBRepo awardsMDBRepo;
    final AttachmentSvc attachmentSvc;

    public AwardsSvcImpl(AwardsMDBRepo awardsMDBRepo, AttachmentSvc attachmentSvc) {
        this.awardsMDBRepo = awardsMDBRepo;
        this.attachmentSvc = attachmentSvc;
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
    public AwardDAO getAwardDAOWithAttachments(String awardId) {
        AwardDAO awardDAO = awardsMDBRepo.findById(awardId).map(AwardDAO::new).orElseThrow();
        List<AttachmentDAO> attachments = attachmentSvc.findAttachmentsByNoticeId(awardDAO.getNoticeId());
        return awardDAO.setAttachments(attachments);
    }
}
