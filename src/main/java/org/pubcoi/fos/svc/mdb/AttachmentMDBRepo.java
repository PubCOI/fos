package org.pubcoi.fos.svc.mdb;


import org.pubcoi.cdm.cf.attachments.Attachment;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface AttachmentMDBRepo extends MongoRepository<Attachment, String> {
    List<Attachment> findByNoticeId(String noticeId);
}
