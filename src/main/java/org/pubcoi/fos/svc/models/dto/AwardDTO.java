/*
 * Copyright (c) 2021 PubCOI.org. This file is part of Fos@PubCOI.
 *
 * Fos@PubCOI is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Fos@PubCOI is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Fos@PubCOI.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.pubcoi.fos.svc.models.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import org.pubcoi.fos.svc.models.core.CFAward;
import org.pubcoi.fos.views.FosViews;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Used to return list of awards to the user
 */
public class AwardDTO {

    @JsonView(FosViews.Summary.class)
    String id;
    @JsonView(FosViews.Summary.class)
    String noticeId;
    @JsonView(FosViews.Details.class)
    String noticeTitle;
    @JsonView(FosViews.Summary.class)
    String organisation;
    @JsonView(FosViews.Summary.class)
    String supplierName;
    @JsonView(FosViews.Details.class)
    Long supplierNumTotalAwards;
    @JsonView(FosViews.Details.class)
    Long value;
    @JsonView(FosViews.Details.class)
    Long valueMin;
    @JsonView(FosViews.Details.class)
    Long valueMax;
    @JsonView(FosViews.Details.class)
    LocalDate awardDate;
    @JsonView(FosViews.Details.class)
    LocalDate startDate;
    @JsonView(FosViews.Details.class)
    LocalDate endDate;
    @JsonView(FosViews.WithChildObjects.class)
    List<AttachmentDTO> attachments = new ArrayList<>();

    @JsonProperty("group_award")
    boolean group;

    public AwardDTO() {
    }

    public AwardDTO(CFAward award) {
        this.id = award.getId();
        this.noticeId = award.getNoticeId();
        this.organisation = award.getClient();
        this.supplierName = award.getSupplierName();
        this.value = (null == award.getValue() ? 0 : award.getValue());
        this.valueMin = (null == award.getValueMin() ? 0 : award.getValueMin());
        this.valueMax = (null == award.getValueMax() ? 0 : award.getValueMax());
        this.group = award.getGroup();
        if (null != award.getAwardedDate()) this.awardDate = award.getAwardedDate().toLocalDate();
        if (null != award.getStartDate()) this.startDate = award.getStartDate().toLocalDate();
        if (null != award.getEndDate()) this.endDate = award.getEndDate().toLocalDate();
    }

    public String getId() {
        return id;
    }

    public AwardDTO setId(String id) {
        this.id = id;
        return this;
    }

    public String getOrganisation() {
        return organisation;
    }

    public AwardDTO setOrganisation(String organisation) {
        this.organisation = organisation;
        return this;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public AwardDTO setSupplierName(String supplierName) {
        this.supplierName = supplierName;
        return this;
    }

    public Long getValue() {
        return value;
    }

    public AwardDTO setValue(Long value) {
        this.value = value;
        return this;
    }

    public Long getValueMin() {
        return valueMin;
    }

    public AwardDTO setValueMin(Long valueMin) {
        this.valueMin = valueMin;
        return this;
    }

    public Long getValueMax() {
        return valueMax;
    }

    public AwardDTO setValueMax(Long valueMax) {
        this.valueMax = valueMax;
        return this;
    }

    public String getNoticeId() {
        return noticeId;
    }

    public AwardDTO setNoticeId(String noticeId) {
        this.noticeId = noticeId;
        return this;
    }

    public boolean isGroup() {
        return group;
    }

    public AwardDTO setGroup(boolean group) {
        this.group = group;
        return this;
    }

    public List<AttachmentDTO> getAttachments() {
        return attachments;
    }

    public AwardDTO setAttachments(List<AttachmentDTO> attachments) {
        this.attachments = attachments;
        return this;
    }

    public Long getSupplierNumTotalAwards() {
        return supplierNumTotalAwards;
    }

    public AwardDTO setSupplierNumTotalAwards(Long supplierNumTotalAwards) {
        this.supplierNumTotalAwards = supplierNumTotalAwards;
        return this;
    }

    public String getNoticeTitle() {
        return noticeTitle;
    }

    public AwardDTO setNoticeTitle(String noticeTitle) {
        this.noticeTitle = noticeTitle;
        return this;
    }

    public LocalDate getAwardDate() {
        return awardDate;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }
}
