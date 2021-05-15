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
import org.pubcoi.fos.svc.models.core.DRResolvePotentialCOITask;
import org.pubcoi.fos.svc.models.dto.tasks.ResolveCOIActionEnum;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.OffsetDateTime;

@Document(collection = "fos_potential_cois")
public class PotentialConflict {

    String id;
    String sourceId;
    String targetId;
    String resolvedByUid;
    OffsetDateTime resolutionDT;
    PotentialConflictTypeEnum conflictType;

    PotentialConflict() {
    }

    public PotentialConflict(DRResolvePotentialCOITask task, ResolveCOIActionEnum action) {
        this.id = task.getId();
        this.resolvedByUid = task.getCompletedBy().getUid();
        this.resolutionDT = task.getCompletedDT();
        this.sourceId = task.getEntity().getFosId();
        this.targetId = task.getLinkedId();
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
}
