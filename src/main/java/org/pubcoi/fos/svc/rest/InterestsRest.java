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

package org.pubcoi.fos.svc.rest;

import org.pubcoi.fos.svc.models.dto.es.MemberInterestsDTO;
import org.pubcoi.fos.svc.services.MnisSvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class InterestsRest {

    final MnisSvc mnisSvc;

    public InterestsRest(MnisSvc mnisSvc) {
        this.mnisSvc = mnisSvc;
    }

    @GetMapping("/api/interests/{mnisMemberId}")
    public MemberInterestsDTO getInterests(@PathVariable Integer mnisMemberId) {
        return mnisSvc.getInterestsDTOForMember(mnisMemberId);
    }

}
