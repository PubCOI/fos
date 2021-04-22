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

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class InterestSearchResponseHitDTO {

    String id;
    String text;
    List<String> fragments = new ArrayList<>();
    LocalDate registeredDate;
    Float valueSum;
    Boolean donation;

    public InterestSearchResponseHitDTO(MemberInterest interest) {
        this.id = interest.getId();
        this.text = interest.getText();
        this.registeredDate = interest.getRegisteredDate();
        this.valueSum = interest.getValueSum();
        this.donation = interest.getDonation();
    }

    public String getId() {
        return id;
    }

    public List<String> getFragments() {
        return fragments;
    }

    public InterestSearchResponseHitDTO setFragments(List<String> fragments) {
        this.fragments = fragments;
        return this;
    }

    public LocalDate getRegisteredDate() {
        return registeredDate;
    }

    public Float getValueSum() {
        return valueSum;
    }

    public Boolean getDonation() {
        return donation;
    }

    public String getText() {
        return text;
    }
}
