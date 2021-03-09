package org.pubcoi.fos.svc.services;

import org.pubcoi.fos.cdm.attachments.Attachment;

import java.io.IOException;

public interface BatchExecutorSvc {

    void runBatch(Attachment attachment) throws IOException, Exception;

}
