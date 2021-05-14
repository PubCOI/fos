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

package org.pubcoi.fos.svc.models.dto.tasks;

import org.pubcoi.fos.svc.models.core.DRTask;
import org.pubcoi.fos.svc.models.core.FosOrganisation;
import org.pubcoi.fos.svc.models.es.MemberInterest;

public class ResolvePotentialCOIDTO extends TaskDTO {

    FosOrganisation organisation;
    MemberInterest memberInterest;

    ResolvePotentialCOIDTO() {
    }

    public ResolvePotentialCOIDTO(DRTask task, FosOrganisation organisation, MemberInterest interest) {
        super(task);
        this.memberInterest = interest;
        this.organisation = organisation;
    }

    public FosOrganisation getOrganisation() {
        return organisation;
    }

    public MemberInterest getMemberInterest() {
        return memberInterest;
    }
}
