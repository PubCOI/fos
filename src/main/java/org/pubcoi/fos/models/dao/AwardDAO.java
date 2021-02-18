package org.pubcoi.fos.models.dao;

import org.pubcoi.fos.models.cf.AwardDetailParentType;
import org.pubcoi.fos.models.cf.FullNotice;

import java.util.ArrayList;
import java.util.List;

/**
 * Used to return list of awards to the user
 */
public class AwardDAO {

    String id;
    String client;
    List<SupplierDAO> suppliers = new ArrayList<>();

    public AwardDAO() {
    }

    public AwardDAO(FullNotice notice) {
        this.id = notice.getId();
        this.client = notice.getNotice().getContactDetails().getName();
        for (AwardDetailParentType.AwardDetail award : notice.getAwards().getAwardDetail()) {
            suppliers.add(new SupplierDAO()
                    .setCompanyName(award.getSupplierName())
                    .setCompanyNumber(award.getReference())
                    .setValue(award.getValue())
            );
        }
    }

    public String getId() {
        return id;
    }

    public List<SupplierDAO> getSuppliers() {
        return suppliers;
    }

    public String getClient() {
        return client;
    }

    static class SupplierDAO {
        String companyName;
        String companyNumber;
        Long value;

        public String getCompanyName() {
            return companyName;
        }

        public SupplierDAO setCompanyName(String companyName) {
            this.companyName = companyName;
            return this;
        }

        public String getCompanyNumber() {
            return companyNumber;
        }

        public SupplierDAO setCompanyNumber(String companyNumber) {
            this.companyNumber = companyNumber;
            return this;
        }

        public Long getValue() {
            return value;
        }

        public SupplierDAO setValue(Long value) {
            this.value = value;
            return this;
        }
    }
}
