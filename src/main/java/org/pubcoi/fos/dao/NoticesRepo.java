package org.pubcoi.fos.dao;

import org.pubcoi.fos.models.cf.FullNotice;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface NoticesRepo extends MongoRepository<FullNotice, String> {

}
