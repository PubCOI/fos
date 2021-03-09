package org.pubcoi.fos.svc.services;

import org.pubcoi.fos.cdm.attachments.Attachment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;

import java.io.*;
import java.util.concurrent.TimeUnit;

public class BatchRunnerWrapper implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(BatchRunnerWrapper.class);

    final String attachmentId;

    final ProcessBuilder pb;

    public BatchRunnerWrapper(String javaPath, String jarPath, String configPath, Attachment attachment) {
        this.attachmentId = attachment.getId();
        this.pb = new ProcessBuilder(
                javaPath, "-jar", jarPath,
                String.format("--spring.config.location=\"file://%s\"", configPath),
                String.format("attachment_id=%s", attachmentId)
        );
    }

    public BatchRunnerWrapper(String javaPath, Resource javaJar, Resource batchProperties, Resource localProperties, Attachment attachment) throws IOException {
        this.attachmentId = attachment.getId();
        StringBuilder profiles = new StringBuilder("batch"); // default
        StringBuilder confLocations = new StringBuilder(String.format("file://%s", batchProperties.getFile().getAbsolutePath()));
        if (localProperties.exists()) {
            profiles.append(",local");
            confLocations.append(String.format(",file://%s", localProperties.getFile().getAbsolutePath()));
        }
        this.pb = new ProcessBuilder(
                javaPath,
                String.format("-Dspring.profiles.active=%s", profiles.toString()),
                "-jar", javaJar.getFile().getAbsolutePath(),
                String.format("--spring.config.location=%s", confLocations.toString()),
                String.format("attachment_id=%s", attachmentId)
        );
    }

    @Override
    public void run() {
        try {
            logger.info("Starting process with arguments " + String.join(" ", pb.command()));
            Process process = pb.start();
            try {
                boolean exited = process.waitFor(1, TimeUnit.SECONDS);
                // process should have started by now, check if it's running
                if (exited) {
                    logger.error("Error starting process, exit value " + process.exitValue());
                    throw new RuntimeException("Unable to start process");
                }
            } catch (InterruptedException e) {
                logger.error(e.getMessage(), e);
            }
            LogReader lr = new LogReader(process.getInputStream());
            Thread t = new Thread(lr, "BatchRunner");
            t.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


class LogReader implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(BatchRunnerWrapper.class);

    private BufferedReader reader;

    public LogReader(InputStream is) {
        this.reader = new BufferedReader(new InputStreamReader(is));
    }

    public void run() {
        try {
            String line = reader.readLine();
            while (line != null) {
                logger.info(line);
                line = reader.readLine();
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}