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

package org.pubcoi.fos.svc.models.dto.es;

import org.pubcoi.fos.svc.models.es.MemberInterest;
import org.pubcoi.fos.svc.models.es.SourceEnum;

import java.time.LocalDate;

public class MemberInterestDTO {

    Integer category;
    String description;
    SourceEnum source;
    String text;
    Boolean donation = false;
    String donor;
    LocalDate registeredDate;

    MemberInterestDTO() {}

    public MemberInterestDTO(MemberInterest interest) {
        this.source = interest.getSource();
        this.text = interest.getText();
        this.donor = interest.getDonorName();
        this.registeredDate = interest.getRegisteredDate();
        if (null != interest.getDonation()) {
            this.donation = interest.getDonation();
        }
        this.category = (null != interest.getMnisCategory() ? interest.getMnisCategory() : interest.getPwCategory());
        this.description = (null != interest.getMnisCategoryDescription() ? interest.getMnisCategoryDescription() : interest.getPwCategoryDescription());
    }

    public Integer getCategory() {
        return category;
    }

    public MemberInterestDTO setCategory(Integer category) {
        this.category = category;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public MemberInterestDTO setDescription(String description) {
        this.description = description;
        return this;
    }

    public SourceEnum getSource() {
        return source;
    }

    public MemberInterestDTO setSource(SourceEnum source) {
        this.source = source;
        return this;
    }

    public String getText() {
        return text;
    }

    public MemberInterestDTO setText(String text) {
        this.text = text;
        return this;
    }

    public Boolean getDonation() {
        return donation;
    }

    public MemberInterestDTO setDonation(Boolean donation) {
        this.donation = donation;
        return this;
    }

    public String getDonor() {
        return donor;
    }

    public MemberInterestDTO setDonor(String donor) {
        this.donor = donor;
        return this;
    }

    public LocalDate getRegisteredDate() {
        return registeredDate;
    }

    public MemberInterestDTO setRegisteredDate(LocalDate registeredDate) {
        this.registeredDate = registeredDate;
        return this;
    }
}
