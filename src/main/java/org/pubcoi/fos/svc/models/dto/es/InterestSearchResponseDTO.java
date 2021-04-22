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

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class InterestSearchResponseDTO implements ESResultInterface {

    Integer mnisPersonId;
    String personName;
    @JsonProperty("top_hits")
    List<InterestSearchResponseHitDTO> topHits = new ArrayList<>();

    InterestSearchResponseDTO() {}

    public InterestSearchResponseDTO(String name) {
        this.personName = name;
    }

    public String getPersonName() {
        return personName;
    }

    public InterestSearchResponseDTO setPersonName(String personName) {
        this.personName = personName;
        return this;
    }

    public List<InterestSearchResponseHitDTO> getTopHits() {
        return topHits;
    }

    public InterestSearchResponseDTO setTopHits(List<InterestSearchResponseHitDTO> topHits) {
        this.topHits = topHits;
        return this;
    }

    public Integer getMnisPersonId() {
        return mnisPersonId;
    }

    public InterestSearchResponseDTO setMnisPersonId(Integer mnisPersonId) {
        this.mnisPersonId = mnisPersonId;
        return this;
    }
}
