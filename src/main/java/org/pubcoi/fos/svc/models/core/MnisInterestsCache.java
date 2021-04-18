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
