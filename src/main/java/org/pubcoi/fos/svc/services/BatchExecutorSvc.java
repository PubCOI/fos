package org.pubcoi.fos.svc.services;

import org.pubcoi.cdm.cf.attachments.Attachment;

import java.io.IOException;

public interface BatchExecutorSvc {

    void runBatch(Attachment attachment) throws IOException, Exception;

}
