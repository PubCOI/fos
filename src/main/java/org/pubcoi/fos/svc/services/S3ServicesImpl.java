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

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import org.pubcoi.cdm.cf.attachments.Attachment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.net.URL;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Service
public class S3ServicesImpl implements S3Services {
    private static final Logger logger = LoggerFactory.getLogger(S3ServicesImpl.class);

    @Value("${fos.s3.region:eu-west-2}")
    String awsRegionStr;

    @Value("${fos.s3.bucket:pubcoi-fos}")
    String awsBucket;

    @Value("${fos.s3.access-key:changeme}")
    String awsAccessKey;

    @Value("${fos.s3.secret-key:changeme}")
    String awsSecretKey;

    @Value("${fos.s3.access-expiry-sec:300}")
    Integer accessExpiresSec;

    AmazonS3 s3Client;

    @PostConstruct
    public void postConstruct() {
        s3Client = AmazonS3Client
                .builder()
                .withRegion(awsRegionStr)
                .withCredentials(
                        new AWSStaticCredentialsProvider(new AWSCredentials() {
                            @Override
                            public String getAWSAccessKeyId() {
                                logger.trace("Using AWS access key {}", awsAccessKey);
                                return awsAccessKey;
                            }

                            @Override
                            public String getAWSSecretKey() {
                                return awsSecretKey;
                            }
                        })
                ).build();
    }

    @Override
    public URL getSignedURL(Attachment item) {
        final String key = String.format("cf/attachments/%s/ocr", item.getId());
        logger.debug("Generating signed URL for key {}", key);
        return s3Client.generatePresignedUrl(awsBucket, key, Date.from(Instant.now().plus(accessExpiresSec, ChronoUnit.SECONDS)));
    }
}
