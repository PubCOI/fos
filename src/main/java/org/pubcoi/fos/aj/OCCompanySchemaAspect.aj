package org.pubcoi.fos.aj;

import com.opencorporates.schemas.OCCompanySchema;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.mapping.Document;

@Configuration
public aspect OCCompanySchemaAspect {

    declare @type: com.opencorporates.schemas.OCCompanySchema :@Document(collection = "oc_companies");

    String OCCompanySchema.id;
    void com.opencorporates.schemas.OCCompanySchema .setId(String id) {
        this.id = id;
    }

    before(): execution(com.opencorporates.schemas.OCCompanySchema.new(..)) && !within(OCCompanySchemaAspect) {
        System.out.println("running constructor method");
    }

    //    public OCCompany() {
//        this.id = String.format("company_%s_%s", this.getJurisdictionCode(), this.getCompanyNumber());
//    }

}
