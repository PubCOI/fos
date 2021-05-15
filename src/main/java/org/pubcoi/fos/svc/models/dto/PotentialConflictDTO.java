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

package org.pubcoi.fos.svc.models.dto;

import org.pubcoi.fos.svc.models.core.FosUser;
import org.pubcoi.fos.svc.models.mdb.PotentialConflict;
import org.pubcoi.fos.svc.models.mdb.PotentialConflictTypeEnum;
import org.pubcoi.fos.svc.models.neo.nodes.OrganisationNode;
import org.pubcoi.fos.svc.models.neo.nodes.PersonNode;

public class PotentialConflictDTO {

    String id;
    String sourceId;
    String sourceName;
    String targetId;
    String targetName;
    String text;
    String resolvedBy;
    PotentialConflictTypeEnum conflictType;

    PotentialConflictDTO() {}

    public PotentialConflictDTO(PotentialConflict potentialConflict, FosUser user, PersonNode source, OrganisationNode target) {
        this.id = potentialConflict.getId();
        this.sourceId = potentialConflict.getSourceId();
        this.targetId = potentialConflict.getTargetId();
        this.sourceName = source.getCommonName();
        this.targetName = target.getName();
        this.conflictType = potentialConflict.getConflictType();
        this.resolvedBy = user.getDisplayName();
    }

    public String getSourceId() {
        return sourceId;
    }

    public String getSourceName() {
        return sourceName;
    }

    public String getTargetId() {
        return targetId;
    }

    public String getTargetName() {
        return targetName;
    }

    public String getText() {
        return text;
    }

    public String getResolvedBy() {
        return resolvedBy;
    }

    public String getId() {
        return id;
    }

    public PotentialConflictTypeEnum getConflictType() {
        return conflictType;
    }
}
