package org.pubcoi.fos.svc.mdb;

import org.pubcoi.fos.svc.models.core.DRTask;
import org.pubcoi.fos.svc.models.core.FosTaskType;
import org.pubcoi.fos.svc.models.neo.nodes.FosEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TasksRepo extends MongoRepository<DRTask, String> {
    DRTask getById(String taskId);
    DRTask getByTaskTypeAndEntity(FosTaskType taskType, FosEntity entity);
}
