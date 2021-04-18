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

package org.pubcoi.fos.svc.models.core;

import org.pubcoi.cdm.mnis.MnisMemberType;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.OffsetDateTime;

/**
 * Used for caching requests for the member of interests
 *
 */

@Document(collection = "mnis_interests")
public class MnisInterestsCache {

    @Id
    Integer memberId;
    OffsetDateTime lastUpdated;
    MnisMemberType member;

    MnisInterestsCache() {}

    public MnisInterestsCache(MnisMemberType member) {
        this.memberId = member.getMemberId();
        this.member = member;
        this.lastUpdated = OffsetDateTime.now();
    }

    public Integer getMemberId() {
        return memberId;
    }

    public MnisInterestsCache setMemberId(Integer memberId) {
        this.memberId = memberId;
        return this;
    }

    public OffsetDateTime getLastUpdated() {
        return lastUpdated;
    }

    public MnisInterestsCache setLastUpdated(OffsetDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
        return this;
    }

    public MnisMemberType getMember() {
        return member;
    }

    public MnisInterestsCache setMember(MnisMemberType member) {
        this.member = member;
        return this;
    }
}
