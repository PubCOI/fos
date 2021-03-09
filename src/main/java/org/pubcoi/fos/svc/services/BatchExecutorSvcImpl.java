package org.pubcoi.fos.svc.services;

import org.pubcoi.fos.cdm.attachments.Attachment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class BatchExecutorSvcImpl implements BatchExecutorSvc {
    private static final Logger logger = LoggerFactory.getLogger(BatchExecutorSvcImpl.class);

    ExecutorService executorService = Executors.newFixedThreadPool(1);

    @Value("${fos.batch.paths.java:/usr/bin/java}")
    String javaPath;

    @Value("${fos.batch.paths.jar:fos-batch-1.0-SNAPSHOT.jar}")
    Resource javaJar;

    @Value("${fos.batch.paths.application-config:classpath:application-batch.properties}")
    Resource batchProperties;

    @Value("${fos.batch.paths.application-config:classpath:application-local.properties}")
    Resource localProperties;

    @PostConstruct
    public void checkResourcesExist() {
        if (!javaJar.exists()) {
            try {
                logger.error(String.format("%s does not exist", javaJar.getFile().getAbsolutePath()));
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
        }
        if (!batchProperties.exists()) {
            try {
                logger.error(String.format("%s does not exist", batchProperties.getFile().getAbsolutePath()));
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
        }
        if (!localProperties.exists()) {
            logger.warn("application-local.properties does not exist, but will not halt execution...");
        }
        if (!javaJar.exists() || !batchProperties.exists()) {
            throw new RuntimeException("Unable to find required resources");
        }
    }

    @Override
    public void runBatch(Attachment attachment) throws Exception {
        executorService.submit(new BatchRunnerWrapper(javaPath, javaJar, batchProperties, localProperties, attachment));
    }
}
