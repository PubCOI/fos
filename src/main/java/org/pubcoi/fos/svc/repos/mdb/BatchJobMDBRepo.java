package org.pubcoi.fos.svc.repos.mdb;


import org.pubcoi.cdm.batch.BatchJob;
import org.pubcoi.cdm.batch.BatchJobTypeEnum;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface BatchJobMDBRepo extends MongoRepository<BatchJob, String> {

    boolean existsByTargetIdAndType(String targetId, BatchJobTypeEnum type);

}
