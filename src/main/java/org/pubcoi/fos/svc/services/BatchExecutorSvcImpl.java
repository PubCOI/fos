/*
 * Copyright (c) 2021 PubCOI.org. This file is part of Fos@PubCOI.
 *
 * Fos@PubCOI is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Fos@PubCOI is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Fos@PubCOI.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.pubcoi.fos.svc.services;

import org.pubcoi.cdm.cf.attachments.Attachment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
@Profile("batch")
public class BatchExecutorSvcImpl implements BatchExecutorSvc {
    private static final Logger logger = LoggerFactory.getLogger(BatchExecutorSvcImpl.class);

    static ExecutorService executorService = Executors.newFixedThreadPool(1);

    @Value("${fos.batch.paths.java:/usr/bin/java}")
    String javaPath;

    @Value("${fos.batch.paths.jar:fos-batch-1.0-SNAPSHOT.jar}")
    Resource javaJar;

    @Value("${fos.batch.paths.application-batch-config:classpath:application-batch-runtime.properties}")
    Resource batchProperties;

    @Value("${fos.batch.paths.application-local-config:file:config/application-local.properties}")
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
            logger.error(Ansi.Red.format("Found files: jar:%s batchProperties:%s", javaJar.exists(), batchProperties.exists()));
            throw new RuntimeException("Unable to find required resources");
        }
    }

    @Override
    public void runBatch(Attachment attachment) throws Exception {
        executorService.submit(new BatchRunnerWrapper(javaPath, javaJar, batchProperties, localProperties, attachment));
    }
}
