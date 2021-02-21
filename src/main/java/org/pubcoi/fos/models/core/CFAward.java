package org.pubcoi.fos.models.core;

import org.pubcoi.fos.models.cf.AwardDetailParentType;
import org.pubcoi.fos.models.cf.FullNotice;
import org.pubcoi.fos.models.cf.ReferenceTypeE;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.OffsetDateTime;

/**
 * Basically the same as the notice type, except we use the award GUID as the document ID
 */
@Document(collection = "cf_awards")
public class CFAward {

    /**
     * Should be the CFAward GUID
     */
    @Id
    String id;
    String noticeID;
    Long valueMin;
    Long valueMax;
    Long value;
    String client;
    OffsetDateTime startDate;
    OffsetDateTime endDate;
    OffsetDateTime awardedDate;
    ReferenceTypeE orgReferenceType;
    String orgReference;
    FOSOrganisation fosOrganisation;
    String supplierName;
    String supplierAddress;

    public CFAward() {}

    public CFAward(FullNotice notice, AwardDetailParentType.AwardDetail awardDetail) {
        this.id = awardDetail.getAwardGuid();
        this.noticeID = awardDetail.getNoticeId();
        this.valueMin = (null != notice.getNotice().getValueLow()) ? notice.getNotice().getValueLow().longValue() : null;
        this.valueMax = (null != notice.getNotice().getValueHigh()) ? notice.getNotice().getValueHigh().longValue() : null;
        this.client = notice.getNotice().getOrganisationName();
        this.value = awardDetail.getValue();
        this.startDate = awardDetail.getStartDate();
        this.endDate = awardDetail.getEndDate();
        this.awardedDate = awardDetail.getAwardedDate();
        this.orgReferenceType = awardDetail.getReferenceType();
        this.orgReference = awardDetail.getReference();
        this.supplierName = awardDetail.getSupplierName();
        this.supplierAddress = awardDetail.getSupplierAddress();
    }

    public String getId() {
        return id;
    }

    public CFAward setId(String id) {
        this.id = id;
        return this;
    }

    public String getNoticeID() {
        return noticeID;
    }

    public CFAward setNoticeID(String noticeID) {
        this.noticeID = noticeID;
        return this;
    }

    public Long getValueMin() {
        return valueMin;
    }

    public CFAward setValueMin(Long valueMin) {
        this.valueMin = valueMin;
        return this;
    }

    public Long getValueMax() {
        return valueMax;
    }

    public CFAward setValueMax(Long valueMax) {
        this.valueMax = valueMax;
        return this;
    }

    public Long getValue() {
        return value;
    }

    public CFAward setValue(Long value) {
        this.value = value;
        return this;
    }

    public OffsetDateTime getStartDate() {
        return startDate;
    }

    public CFAward setStartDate(OffsetDateTime startDate) {
        this.startDate = startDate;
        return this;
    }

    public OffsetDateTime getEndDate() {
        return endDate;
    }

    public CFAward setEndDate(OffsetDateTime endDate) {
        this.endDate = endDate;
        return this;
    }

    public OffsetDateTime getAwardedDate() {
        return awardedDate;
    }

    public CFAward setAwardedDate(OffsetDateTime awardedDate) {
        this.awardedDate = awardedDate;
        return this;
    }

    public ReferenceTypeE getOrgReferenceType() {
        return orgReferenceType;
    }

    public CFAward setOrgReferenceType(ReferenceTypeE orgReferenceType) {
        this.orgReferenceType = orgReferenceType;
        return this;
    }

    public String getOrgReference() {
        return orgReference;
    }

    public CFAward setOrgReference(String orgReference) {
        this.orgReference = orgReference;
        return this;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public CFAward setSupplierName(String supplierName) {
        this.supplierName = supplierName;
        return this;
    }

    public String getSupplierAddress() {
        return supplierAddress;
    }

    public CFAward setSupplierAddress(String supplierAddress) {
        this.supplierAddress = supplierAddress;
        return this;
    }

    @Override
    public String toString() {
        return "CFAward{" +
                "id='" + id + '\'' +
                '}';
    }

    public FOSOrganisation getFosOrganisation() {
        return fosOrganisation;
    }

    public CFAward setFosOrganisation(FOSOrganisation fosOrganisation) {
        this.fosOrganisation = fosOrganisation;
        return this;
    }

    public String getClient() {
        return client;
    }

    public CFAward setClient(String client) {
        this.client = client;
        return this;
    }
}
