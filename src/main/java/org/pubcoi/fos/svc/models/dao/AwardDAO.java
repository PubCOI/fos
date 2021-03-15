package org.pubcoi.fos.svc.models.dao;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import org.pubcoi.fos.svc.models.core.CFAward;

import java.util.ArrayList;
import java.util.List;

/**
 * Used to return list of awards to the user
 */
public class AwardDAO {

    @JsonView(Views.Summary.class)
    String id;
    @JsonView(Views.Summary.class)
    String noticeId;
    @JsonView(Views.Summary.class)
    String organisation;
    @JsonView(Views.Summary.class)
    String supplierName;
    @JsonView(Views.Details.class)
    Long supplierNumTotalAwards;
    @JsonView(Views.Details.class)
    Long value;
    @JsonView(Views.Details.class)
    Long valueMin;
    @JsonView(Views.Details.class)
    Long valueMax;
    @JsonView(Views.WithChildObjects.class)
    List<AttachmentDAO> attachments = new ArrayList<>();

    @JsonProperty("group_award")
    boolean group;

    public AwardDAO() {
    }

    public AwardDAO(CFAward award) {
        this.id = award.getId();
        this.noticeId = award.getNoticeId();
        this.organisation = award.getClient();
        this.supplierName = award.getSupplierName();
        this.value = (null == award.getValue() ? 0 : award.getValue());
        this.valueMin = (null == award.getValueMin() ? 0 : award.getValueMin());
        this.valueMax = (null == award.getValueMax() ? 0 : award.getValueMax());
        this.group = award.getGroup();
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

    public String getNoticeId() {
        return noticeId;
    }

    public AwardDAO setNoticeId(String noticeId) {
        this.noticeId = noticeId;
        return this;
    }

    public boolean isGroup() {
        return group;
    }

    public AwardDAO setGroup(boolean group) {
        this.group = group;
        return this;
    }

    public List<AttachmentDAO> getAttachments() {
        return attachments;
    }

    public AwardDAO setAttachments(List<AttachmentDAO> attachments) {
        this.attachments = attachments;
        return this;
    }

    public Long getSupplierNumTotalAwards() {
        return supplierNumTotalAwards;
    }

    public AwardDAO setSupplierNumTotalAwards(Long supplierNumTotalAwards) {
        this.supplierNumTotalAwards = supplierNumTotalAwards;
        return this;
    }
}
