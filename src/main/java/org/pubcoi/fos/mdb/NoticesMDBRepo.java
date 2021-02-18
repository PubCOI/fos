package org.pubcoi.fos.mdb;

import org.pubcoi.fos.models.cf.FullNotice;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface NoticesMDBRepo extends MongoRepository<FullNotice, String> {

}
