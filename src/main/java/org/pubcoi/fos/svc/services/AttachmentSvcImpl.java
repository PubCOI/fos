package org.pubcoi.fos.svc.services;

import org.pubcoi.fos.svc.repos.mdb.AttachmentMDBRepo;
import org.pubcoi.fos.svc.models.dao.AttachmentDAO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AttachmentSvcImpl implements AttachmentSvc {

    final AttachmentMDBRepo attachmentMDBRepo;

    public AttachmentSvcImpl(AttachmentMDBRepo attachmentMDBRepo) {
        this.attachmentMDBRepo = attachmentMDBRepo;
    }

    @Override
    public List<AttachmentDAO> findAttachmentsByNoticeId(String noticeId) {
        return attachmentMDBRepo.findByNoticeId(noticeId).stream()
                .map(AttachmentDAO::new)
                .collect(Collectors.toList());
    }
}
