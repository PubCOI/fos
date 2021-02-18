package org.pubcoi.fos.models.core;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "foi_tasks")
public class DRTask {

    @Id
    String taskID;

    DRTaskType taskType;

}
