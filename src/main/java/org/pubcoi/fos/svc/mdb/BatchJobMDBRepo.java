package org.pubcoi.fos.svc.mdb;


import org.pubcoi.fos.cdm.batch.BatchJob;
import org.pubcoi.fos.cdm.batch.BatchJobTypeEnum;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface BatchJobMDBRepo extends MongoRepository<BatchJob, String> {

    boolean existsByTargetIdAndType(String targetId, BatchJobTypeEnum type);

}
