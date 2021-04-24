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

import org.pubcoi.fos.svc.exceptions.FosException;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.Map;

/**
 * Used when returning list of awards on 'datasets' view
 * Embeds LEGAL_ENTITY entries (so you can clearly see what the company is known as)
 */
public class AwardsListResponseDTO {

    String id;
    String noticeId;
    String client;
    String awardee;
    Long value;
    Long valueMin;
    Long valueMax;
    LocalDate awardDate;
    LocalDate startDate;
    LocalDate endDate;
    Boolean groupAward;
    KnownAsDTO knownAs;

    AwardsListResponseDTO() {}

    public AwardsListResponseDTO(AwardsGraphResponse graphResponse) {
        if (null == graphResponse.getAwardNode()) throw new FosException("Cannot deserialise response");
        Map<String, Object> award = graphResponse.getAwardNode().asMap();
        Map<String, Object> awardee = graphResponse.getAwardee().asMap();
        Map<String, Object> awardOrg = graphResponse.getAwardOrgLink().asMap();
        this.id = (String) award.get("id");
        this.client = graphResponse.getClientName().asString();
        this.awardee = (String) awardee.get("name");
        this.value = (Long) award.get("value");
        this.awardDate = ((ZonedDateTime) awardOrg.get("awardedDate")).toLocalDate();
        this.startDate = ((ZonedDateTime) awardOrg.get("startDate")).toLocalDate();
        this.endDate = ((ZonedDateTime) awardOrg.get("endDate")).toLocalDate();
        this.groupAward = (Boolean) award.get("groupAward");
        if (null != graphResponse.getLegalEntity()) {
            this.knownAs = new KnownAsDTO(
                    (String) graphResponse.getLegalEntity().asMap().get("id"),
                    (String) graphResponse.getLegalEntity().asMap().get("name")
            );
        }
    }

    public String getId() {
        return id;
    }

    public AwardsListResponseDTO setId(String id) {
        this.id = id;
        return this;
    }

    public String getClient() {
        return client;
    }

    public AwardsListResponseDTO setClient(String client) {
        this.client = client;
        return this;
    }

    public String getAwardee() {
        return awardee;
    }

    public AwardsListResponseDTO setAwardee(String awardee) {
        this.awardee = awardee;
        return this;
    }

    public Long getValue() {
        return value;
    }

    public AwardsListResponseDTO setValue(Long value) {
        this.value = value;
        return this;
    }

    public LocalDate getAwardDate() {
        return awardDate;
    }

    public AwardsListResponseDTO setAwardDate(LocalDate awardDate) {
        this.awardDate = awardDate;
        return this;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public AwardsListResponseDTO setStartDate(LocalDate startDate) {
        this.startDate = startDate;
        return this;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public AwardsListResponseDTO setEndDate(LocalDate endDate) {
        this.endDate = endDate;
        return this;
    }

    public Boolean getGroupAward() {
        return groupAward;
    }

    public AwardsListResponseDTO setGroupAward(Boolean groupAward) {
        this.groupAward = groupAward;
        return this;
    }

    public KnownAsDTO getKnownAs() {
        return knownAs;
    }

    public AwardsListResponseDTO setKnownAs(KnownAsDTO knownAs) {
        this.knownAs = knownAs;
        return this;
    }

    public Long getValueMin() {
        return valueMin;
    }

    public AwardsListResponseDTO setValueMin(Long valueMin) {
        this.valueMin = valueMin;
        return this;
    }

    public Long getValueMax() {
        return valueMax;
    }

    public AwardsListResponseDTO setValueMax(Long valueMax) {
        this.valueMax = valueMax;
        return this;
    }

    public String getNoticeId() {
        return noticeId;
    }

    public AwardsListResponseDTO setNoticeId(String noticeId) {
        this.noticeId = noticeId;
        return this;
    }

    private class KnownAsDTO {

        KnownAsDTO() {}

        public KnownAsDTO(String id, String name) {
            this.id = id;
            this.name = name;
        }

        String id;
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
