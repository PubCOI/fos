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

package org.pubcoi.fos.svc.models.queries;

import org.pubcoi.fos.svc.models.core.CFAward;
import org.pubcoi.fos.svc.models.dto.AwardDTO;
import org.pubcoi.fos.svc.models.dto.OrganisationDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class OrganisationsGraphListResponseDTO {

    OrganisationDTO organisation;
    int totalAwards = 0;
    List<String> awards = new ArrayList<>();
    List<AwardDTO> awardDetails = new ArrayList<>();

    OrganisationsGraphListResponseDTO() {
    }

    public OrganisationsGraphListResponseDTO(Map<String, Object> orgNodeMap, int size, List<String> awards, Iterable<CFAward> awardDetails) {
        this.organisation = new OrganisationDTO(
                (String) orgNodeMap.get("fosId"),
                (String) orgNodeMap.get("name"),
                (Boolean) orgNodeMap.get("verified")
        );
        // if award details get removed from the system, we still want to know which awards they were
        this.totalAwards = size;
        this.awards = awards;
        for (CFAward award : awardDetails) {
            this.awardDetails.add(new AwardDTO(award));
        }
    }

    public OrganisationDTO getOrganisation() {
        return organisation;
    }

    public OrganisationsGraphListResponseDTO setOrganisation(OrganisationDTO organisation) {
        this.organisation = organisation;
        return this;
    }

    public List<AwardDTO> getAwardDetails() {
        return awardDetails;
    }

    public OrganisationsGraphListResponseDTO setAwardDetails(List<AwardDTO> awardDetails) {
        this.awardDetails = awardDetails;
        return this;
    }

    public int getTotalAwards() {
        return totalAwards;
    }

    public OrganisationsGraphListResponseDTO setTotalAwards(int totalAwards) {
        this.totalAwards = totalAwards;
        return this;
    }

    public List<String> getAwards() {
        return awards;
    }

    public OrganisationsGraphListResponseDTO setAwards(List<String> awards) {
        this.awards = awards;
        return this;
    }
}
