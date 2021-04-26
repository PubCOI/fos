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

import org.springframework.util.DigestUtils;

import static org.pubcoi.fos.svc.services.Utils.normalise;

/**
 * Use this class to store orgs on fos_organisations that cannot be manually resolved
 * Once resolved to a proper company, this should be converted to a FosCanonicalOrg type
 * NOTE that the hash method is unsafe and so NO assumptions should be made re how many
 * other nodes refer to a single FUO instance
 */
public class FosNonCanonicalOrg extends FosOrganisation {
    FosNonCanonicalOrg() {
    }

    public static String resolveId(CFAward award) {
        return String.format("hash:%s", DigestUtils.md5DigestAsHex(
                (normalise(award.getOrgReference()) +
                        normalise(null != award.getOrgReferenceType() ? award.getOrgReferenceType().toString() : "") +
                        normalise(award.getSupplierName()) +
                        normalise(award.getSupplierAddress())
                ).getBytes()));
    }

    public FosNonCanonicalOrg(CFAward award) {
        this.fosId = resolveId(award);
        this.reference = award.getOrgReference();
        this.referenceType = (null != award.getOrgReferenceType()) ? award.getOrgReferenceType().toString() : null;
        this.companyName = award.getSupplierName();
        this.companyAddress = award.getSupplierAddress();
    }

    @Override
    public String toString() {
        return "FosNonCanonicalOrg{" +
                "id='" + fosId + '\'' +
                '}';
    }
}
