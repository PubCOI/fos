package org.pubcoi.fos.svc.services;

import org.pubcoi.fos.svc.models.dao.AttachmentDAO;

import java.util.List;

public interface AttachmentSvc {
    List<AttachmentDAO> findAttachmentsByNoticeId(String noticeId);
}
