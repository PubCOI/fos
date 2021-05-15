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

package org.pubcoi.fos.svc.models.mdb;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.pubcoi.fos.svc.models.core.DRResolvePotentialCOITask;
import org.pubcoi.fos.svc.models.dto.tasks.ResolveCOIActionEnum;
import org.pubcoi.fos.svc.models.es.MemberInterest;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.OffsetDateTime;

@Document(collection = "fos_potential_cois")
public class PotentialConflict {

    String id;
    String sourceId;
    String targetId;
    String resolvedByUid;
    String interestId;
    OffsetDateTime resolutionDT;
    PotentialConflictTypeEnum conflictType;

    PotentialConflict() {
    }

    public PotentialConflict(DRResolvePotentialCOITask task, MemberInterest memberInterest, ResolveCOIActionEnum action) {
        this.id = task.getId();
        this.resolvedByUid = task.getCompletedByUid();
        this.resolutionDT = task.getCompletedDT();
        // direction is person->org so linkedId must be source
        this.sourceId = memberInterest.getPersonNodeId();
        this.targetId = task.getEntity().getFosId();
        this.interestId = memberInterest.getId();
        this.conflictType = action.equals(ResolveCOIActionEnum.flag) ? PotentialConflictTypeEnum.flagged : PotentialConflictTypeEnum.false_positive;
    }

    public String getId() {
        return id;
    }

    public String getSourceId() {
        return sourceId;
    }

    public String getTargetId() {
        return targetId;
    }

    public String getResolvedByUid() {
        return resolvedByUid;
    }

    public OffsetDateTime getResolutionDT() {
        return resolutionDT;
    }

    public PotentialConflictTypeEnum getConflictType() {
        return conflictType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        PotentialConflict that = (PotentialConflict) o;

        return new EqualsBuilder().append(id, that.id).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(id).toHashCode();
    }

    public String getInterestId() {
        return interestId;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("id", id)
                .append("sourceId", sourceId)
                .append("targetId", targetId)
                .append("resolvedByUid", resolvedByUid)
                .append("interestId", interestId)
                .append("resolutionDT", resolutionDT)
                .append("conflictType", conflictType)
                .toString();
    }
}
