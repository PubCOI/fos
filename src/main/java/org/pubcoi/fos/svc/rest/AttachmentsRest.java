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

package org.pubcoi.fos.svc.rest;

import org.pubcoi.cdm.cf.attachments.Attachment;
import org.pubcoi.fos.svc.exceptions.endpoint.FosEndpointBadRequestException;
import org.pubcoi.fos.svc.models.dto.AttachmentDTO;
import org.pubcoi.fos.svc.repos.mdb.AttachmentMDBRepo;
import org.pubcoi.fos.svc.services.S3Services;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.net.URISyntaxException;

@RestController
public class AttachmentsRest {

    final S3Services s3Services;
    final AttachmentMDBRepo attachmentMDBRepo;

    public AttachmentsRest(S3Services s3Services, AttachmentMDBRepo attachmentMDBRepo) {
        this.s3Services = s3Services;
        this.attachmentMDBRepo = attachmentMDBRepo;
    }

    @GetMapping("/api/attachments/{attachmentId}/view")
    public ResponseEntity<String> viewRedirect(
            @PathVariable String attachmentId
    ) {
        Attachment attachment = attachmentMDBRepo.findById(attachmentId).orElseThrow(() -> new FosEndpointBadRequestException("Unable to find attachment"));
        // todo - check that we've got the location on the object ... for now just return where we think the doc should be
        // attachment.getS3Locations()
        try {
            return ResponseEntity
                    .status(HttpStatus.TEMPORARY_REDIRECT)
                    .location(s3Services.getSignedURL(attachment).toURI())
                    .build();
        } catch (URISyntaxException e) {
            throw new FosEndpointBadRequestException("Unable to get URL");
        }
    }

    @GetMapping("/api/attachments/{attachmentId}/metadata")
    public AttachmentDTO getAttachmentMetadata(
            @PathVariable String attachmentId
    ) {
        Attachment attachment = attachmentMDBRepo.findById(attachmentId).orElseThrow(() -> new FosEndpointBadRequestException("Unable to find attachment"));
        return new AttachmentDTO(attachment);
    }
}
