package org.pubcoi.fos.svc.services;

import org.pubcoi.cdm.cf.attachments.Attachment;

import java.net.URL;

public interface S3Services {
    URL getSignedURL(Attachment item);
}
