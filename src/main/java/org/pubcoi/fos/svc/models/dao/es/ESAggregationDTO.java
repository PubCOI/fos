package org.pubcoi.fos.svc.models.dao.es;

import org.pubcoi.cdm.cf.FullNotice;
import org.pubcoi.cdm.cf.attachments.Attachment;

public class ESAggregationDTO extends ESResult {
    public ESAggregationDTO() {
    }

    public ESAggregationDTO(Attachment attachment, FullNotice notice) {
        super(attachment, notice);
    }
}
