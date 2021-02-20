package org.pubcoi.fos.mdb;

import org.pubcoi.fos.models.core.DRTask;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TasksRepo extends MongoRepository<DRTask, String> {
    DRTask getById(String taskID);
}
