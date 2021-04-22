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

import org.pubcoi.cdm.mnis.MnisMemberType;

import java.util.ArrayList;
import java.util.List;

public class MemberInterestsDTO {

    String personName;
    String personNodeId;
    String pwPersonId;
    Integer mnisPersonId;
    List<MemberInterestDTO> interests = new ArrayList<>();

    MemberInterestsDTO() {}

    public MemberInterestsDTO(MnisMemberType memberType) {
        this.mnisPersonId = memberType.getMemberId();
        this.personName = memberType.getFullTitle();
        this.pwPersonId = memberType.getPwId();
    }

    public String getPersonName() {
        return personName;
    }

    public MemberInterestsDTO setPersonName(String personName) {
        this.personName = personName;
        return this;
    }

    public String getPersonNodeId() {
        return personNodeId;
    }

    public MemberInterestsDTO setPersonNodeId(String personNodeId) {
        this.personNodeId = personNodeId;
        return this;
    }

    public String getPwPersonId() {
        return pwPersonId;
    }

    public MemberInterestsDTO setPwPersonId(String pwPersonId) {
        this.pwPersonId = pwPersonId;
        return this;
    }

    public Integer getMnisPersonId() {
        return mnisPersonId;
    }

    public MemberInterestsDTO setMnisPersonId(Integer mnisPersonId) {
        this.mnisPersonId = mnisPersonId;
        return this;
    }

    public List<MemberInterestDTO> getInterests() {
        return interests;
    }

    public MemberInterestsDTO setInterests(List<MemberInterestDTO> interests) {
        this.interests = interests;
        return this;
    }
}
