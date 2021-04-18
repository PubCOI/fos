package org.pubcoi.fos.svc.services;

import org.pubcoi.fos.svc.repos.mdb.TasksRepo;
import org.pubcoi.fos.svc.models.core.DRTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class TasksSvcImpl implements TasksSvc {
    private static final Logger logger = LoggerFactory.getLogger(TasksSvcImpl.class);

    TasksRepo tasksRepo;

    public TasksSvcImpl(TasksRepo tasksRepo) {
        this.tasksRepo = tasksRepo;
    }

    @Override
    public void createTask(DRTask task) {
        if (tasksRepo.existsById(task.getId())) {
            logger.warn("Task already created");
        }
        else {
            tasksRepo.save(task.setCompleted(false));
        }
    }
}
