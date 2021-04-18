package org.pubcoi.fos.svc.repos.mdb;

import org.pubcoi.fos.svc.models.mdb.OCCachedQuery;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.OffsetDateTime;

/**
 * Used for caching requests made to OpenCorporates
 */
public interface OCCachedQueryRepo extends MongoRepository<OCCachedQuery, String> {

    OCCachedQuery getByIdAndRequestDTAfter(String id, OffsetDateTime requestDT);

}
