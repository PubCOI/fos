package org.pubcoi.fos.models.dao;

import org.pubcoi.fos.models.core.Award;

/**
 * Used to return list of awards to the user
 */
public class AwardDAO {

    String id;
    String noticeID;
    String organisation;
    String supplierName;
    Long value;
    Long valueMin;
    Long valueMax;

    public AwardDAO() {
    }

    public AwardDAO(Award award) {
        this.id = award.getId();
        this.noticeID = award.getNoticeID();
        this.organisation = award.getClient();
        this.supplierName = award.getSupplierName();
        this.value = (null == award.getValue() ? 0 : award.getValue());
        this.valueMin = (null == award.getValueMin() ? 0 : award.getValueMin());
        this.valueMax = (null == award.getValueMax() ? 0 : award.getValueMax());
    }

    public String getId() {
        return id;
    }

    public String getOrganisation() {
        return organisation;
    }

    public AwardDAO setId(String id) {
        this.id = id;
        return this;
    }

    public AwardDAO setOrganisation(String organisation) {
        this.organisation = organisation;
        return this;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public AwardDAO setSupplierName(String supplierName) {
        this.supplierName = supplierName;
        return this;
    }

    public Long getValue() {
        return value;
    }

    public AwardDAO setValue(Long value) {
        this.value = value;
        return this;
    }

    public Long getValueMin() {
        return valueMin;
    }

    public AwardDAO setValueMin(Long valueMin) {
        this.valueMin = valueMin;
        return this;
    }

    public Long getValueMax() {
        return valueMax;
    }

    public AwardDAO setValueMax(Long valueMax) {
        this.valueMax = valueMax;
        return this;
    }

    public String getNoticeID() {
        return noticeID;
    }

    public AwardDAO setNoticeID(String noticeID) {
        this.noticeID = noticeID;
        return this;
    }
}
