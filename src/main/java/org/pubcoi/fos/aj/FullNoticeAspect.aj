package org.pubcoi.fos.aj;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.mapping.Document;

@Configuration
public aspect FullNoticeAspect {

    declare @type: org.pubcoi.fos.models.cf.FullNotice :@Document(collection = "cf_notices");

}
