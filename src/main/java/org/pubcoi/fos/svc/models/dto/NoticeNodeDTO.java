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

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.pubcoi.cdm.cf.FullNotice;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class NoticeNodeDTO {

    String id;
    String title;
    OffsetDateTime postedDT;
    String organisation;
    String description;
    Double valueLow;
    Double valueHigh;
    Set<AwardDTO> awards = new HashSet<>();

    public NoticeNodeDTO() {}

    public NoticeNodeDTO(FullNotice notice) {
        Objects.requireNonNull(notice.getNotice());
        this.id = notice.getId();
        this.title = notice.getNotice().getTitle();
        this.postedDT = notice.getCreatedDate();
        this.valueLow = notice.getNotice().getValueLow();
        this.valueHigh = notice.getNotice().getValueHigh();
        if (null != notice.getNotice()) {
            this.organisation = notice.getNotice().getOrganisationName();
        }
        if (null != notice.getNotice() && null != notice.getNotice().getTitle() && notice.getNotice().getTitle().length() > 10) {
            this.description = notice.getNotice().getTitle();
        } else if (null != notice.getNotice() && null != notice.getNotice().getDescription() && notice.getNotice().getDescription().length() > 10) {
            this.description = notice.getNotice().getDescription();
        } else if (null != notice.getNotice() && null != notice.getNotice().getCpvDescription() && notice.getNotice().getCpvDescription().length() > 10) {
            this.description = notice.getNotice().getCpvDescription();
        }
    }

    public String getId() {
        return id;
    }

    public NoticeNodeDTO setId(String id) {
        this.id = id;
        return this;
    }

    public OffsetDateTime getPostedDT() {
        return postedDT;
    }

    public NoticeNodeDTO setPostedDT(OffsetDateTime postedDT) {
        this.postedDT = postedDT;
        return this;
    }

    public String getOrganisation() {
        return organisation;
    }

    public NoticeNodeDTO setOrganisation(String organisation) {
        this.organisation = organisation;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public NoticeNodeDTO setDescription(String description) {
        this.description = description;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        NoticeNodeDTO that = (NoticeNodeDTO) o;

        return new EqualsBuilder()
                .append(id, that.id)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .toHashCode();
    }

    public void addAward(AwardDTO awardDTO) {
        this.awards.add(awardDTO);
    }

    public Set<AwardDTO> getAwards() {
        return awards;
    }

    public NoticeNodeDTO setAwards(Set<AwardDTO> awards) {
        this.awards = awards;
        return this;
    }

    public Double getValueLow() {
        return valueLow;
    }

    public Double getValueHigh() {
        return valueHigh;
    }

    public String getTitle() {
        return title;
    }

    public NoticeNodeDTO setTitle(String title) {
        this.title = title;
        return this;
    }

    public NoticeNodeDTO setValueLow(Double valueLow) {
        this.valueLow = valueLow;
        return this;
    }

    public NoticeNodeDTO setValueHigh(Double valueHigh) {
        this.valueHigh = valueHigh;
        return this;
    }
}
