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

import com.fasterxml.jackson.annotation.JsonProperty;
import org.pubcoi.fos.svc.exceptions.endpoint.FosEndpointException;

import java.time.LocalDate;
import java.util.Map;

/**
 * Used when returning list of awards on 'datasets' view
 * Embeds LEGAL_ENTITY entries (so you can clearly see what the company is known as)
 */
public class AwardsGraphListResponseDTO {

    @JsonProperty(required = true)
    String id;
    @JsonProperty(required = true)
    String noticeId;
    String client;
    String awardee;
    Long value;
    Long valueMin;
    Long valueMax;
    @JsonProperty(required = true)
    LocalDate awardDate;
    LocalDate startDate;
    LocalDate endDate;
    Boolean groupAward;
    KnownAsDTO knownAs;

    AwardsGraphListResponseDTO() {}

    public AwardsGraphListResponseDTO(AwardsGraphResponse graphResponse) {
        if (null == graphResponse.getAwardNode()) throw new FosEndpointException("Cannot deserialise response");
        Map<String, Object> award = graphResponse.getAwardNode().asMap();
        Map<String, Object> awardee = graphResponse.getAwardee().asMap();
        Map<String, Object> awardOrg = graphResponse.getAwardOrgLink().asMap();
        this.id = (String) award.get("fosId");
        this.client = graphResponse.getClientName().asString();
        this.awardee = (String) awardee.get("name");
        this.value = (Long) award.get("value");
        this.awardDate = (LocalDate) awardOrg.get("awardedDate");
        this.startDate = (LocalDate) awardOrg.get("startDate");
        this.endDate = (LocalDate) awardOrg.get("endDate");
        this.groupAward = (Boolean) award.get("groupAward");
        if (null != graphResponse.getLegalEntity()) {
            this.knownAs = new KnownAsDTO(
                    (String) graphResponse.getLegalEntity().asMap().get("fosId"),
                    (String) graphResponse.getLegalEntity().asMap().get("name")
            );
        }
    }

    public String getId() {
        return id;
    }

    public AwardsGraphListResponseDTO setId(String id) {
        this.id = id;
        return this;
    }

    public String getClient() {
        return client;
    }

    public AwardsGraphListResponseDTO setClient(String client) {
        this.client = client;
        return this;
    }

    public String getAwardee() {
        return awardee;
    }

    public AwardsGraphListResponseDTO setAwardee(String awardee) {
        this.awardee = awardee;
        return this;
    }

    public Long getValue() {
        return value;
    }

    public AwardsGraphListResponseDTO setValue(Long value) {
        this.value = value;
        return this;
    }

    public LocalDate getAwardDate() {
        return awardDate;
    }

    public AwardsGraphListResponseDTO setAwardDate(LocalDate awardDate) {
        this.awardDate = awardDate;
        return this;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public AwardsGraphListResponseDTO setStartDate(LocalDate startDate) {
        this.startDate = startDate;
        return this;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public AwardsGraphListResponseDTO setEndDate(LocalDate endDate) {
        this.endDate = endDate;
        return this;
    }

    public Boolean getGroupAward() {
        return groupAward;
    }

    public AwardsGraphListResponseDTO setGroupAward(Boolean groupAward) {
        this.groupAward = groupAward;
        return this;
    }

    public KnownAsDTO getKnownAs() {
        return knownAs;
    }

    public AwardsGraphListResponseDTO setKnownAs(KnownAsDTO knownAs) {
        this.knownAs = knownAs;
        return this;
    }

    public Long getValueMin() {
        return valueMin;
    }

    public AwardsGraphListResponseDTO setValueMin(Long valueMin) {
        this.valueMin = valueMin;
        return this;
    }

    public Long getValueMax() {
        return valueMax;
    }

    public AwardsGraphListResponseDTO setValueMax(Long valueMax) {
        this.valueMax = valueMax;
        return this;
    }

    public String getNoticeId() {
        return noticeId;
    }

    public AwardsGraphListResponseDTO setNoticeId(String noticeId) {
        this.noticeId = noticeId;
        return this;
    }

    private class KnownAsDTO {

        KnownAsDTO() {}

        public KnownAsDTO(String id, String name) {
            this.id = id;
            this.name = name;
        }

        @JsonProperty(required = true)
        String id;
        @JsonProperty(required = true)
        String name;

        public String getId() {
            return id;
        }

        public KnownAsDTO setId(String id) {
            this.id = id;
            return this;
        }

        public String getName() {
            return name;
        }

        public KnownAsDTO setName(String name) {
            this.name = name;
            return this;
        }
    }
}
