package org.pubcoi.fos.svc.models.core;

import org.pubcoi.cdm.cf.AwardDetailParentType;
import org.pubcoi.cdm.cf.FullNotice;
import org.pubcoi.cdm.cf.ReferenceTypeE;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.OffsetDateTime;

/**
 * CFAward = Contracts Finder Award Entity.
 * Basically the same as the notice type, except we tidy it up a bit and use the award GUID as the document ID.
 * Will probably get rid of it as it's a bit disgusting.
 */
@Document(collection = "cf_awards")
public class CFAward {

    /**
     * Should be the Award GUID
     */
    @Id
    String id;
    String noticeId;
    Long valueMin;
    Long valueMax;
    Long value;
    String client;
    OffsetDateTime startDate;
    OffsetDateTime endDate;
    OffsetDateTime awardedDate;
    ReferenceTypeE orgReferenceType;
    String orgReference;
    FosOrganisation fosOrganisation;
    String supplierName;
    String supplierAddress;
    Boolean group;

    public CFAward() {}

    public CFAward(FullNotice notice, AwardDetailParentType.AwardDetail awardDetail) {
        this.id = awardDetail.getAwardGuid().toLowerCase();
        this.noticeId = awardDetail.getNoticeId();
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
        // if it's one of a number of awards on the same notice, the data structure will give the cumulative
        // award total to each AwardDetail node
        this.group = notice.getAwards().getAwardDetail().size() > 1;
    }

    public String getId() {
        return id;
    }

    public CFAward setId(String id) {
        this.id = id;
        return this;
    }

    public String getNoticeId() {
        return noticeId;
    }

    public CFAward setNoticeId(String noticeId) {
        this.noticeId = noticeId;
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

    public FosOrganisation getFosOrganisation() {
        return fosOrganisation;
    }

    public CFAward setFosOrganisation(FosOrganisation fosOrganisation) {
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

    public Boolean getGroup() {
        return group;
    }

    public CFAward setGroup(Boolean group) {
        this.group = group;
        return this;
    }
}
