package org.pubcoi.fos.mdb;

import org.pubcoi.fos.cdm.attachments.Attachment;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AttachmentMDBRepo extends MongoRepository<Attachment, String> {
}
