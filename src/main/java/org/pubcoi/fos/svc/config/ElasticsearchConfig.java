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

package org.pubcoi.fos.svc.config;

import org.elasticsearch.action.ingest.PutPipelineRequest;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.bytes.BytesArray;
import org.elasticsearch.common.xcontent.XContentType;
import org.pubcoi.fos.svc.exceptions.core.FosCoreRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;

@Component
public class ElasticsearchConfig {
    private static final Logger logger = LoggerFactory.getLogger(ElasticsearchConfig.class);

    final RestHighLevelClient highLevelClient;

    public ElasticsearchConfig(RestHighLevelClient highLevelClient) {
        this.highLevelClient = highLevelClient;
    }

    @PostConstruct
    public void setup() {
        try {
            byte[] pipeline = Files.readAllBytes(Paths.get(
                    Objects.requireNonNull(getClass().getClassLoader().getResource("es/attachments_pipeline.json")).getFile()
            ));
            PutPipelineRequest request = new PutPipelineRequest("attachment", new BytesArray(pipeline), XContentType.JSON);
            AcknowledgedResponse response = highLevelClient.ingest().putPipeline(request, RequestOptions.DEFAULT);
            logger.info("PUT attachments_pipeline ack: {}", response.isAcknowledged());
            if (!response.isAcknowledged()) {
                throw new FosCoreRuntimeException("Unable to PUT attachments pipeline");
            }
        } catch (IOException e) {
            throw new FosCoreRuntimeException("Unable to open attachments pipeline file");
        }
    }

}
