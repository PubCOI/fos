package org.pubcoi.fos.svc.mdb;

import org.pubcoi.cdm.cf.FullNotice;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface NoticesMDBRepo extends MongoRepository<FullNotice, String> {

}
