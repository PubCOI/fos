package org.pubcoi.fos.models.mdb;

import com.opencorporates.schemas.OCCompanySchema;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "oc_companies")
public class OCCompany extends OCCompanySchema {

    // extends so that we can add customisations
    @Id
    String id;

    public OCCompany() {
        this.id = String.format("company_%s_%s", this.getJurisdictionCode(), this.getCompanyNumber());
    }

}
