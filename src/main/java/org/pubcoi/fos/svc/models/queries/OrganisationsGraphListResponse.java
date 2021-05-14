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

import org.neo4j.driver.internal.value.ListValue;
import org.neo4j.driver.internal.value.NodeValue;

public class OrganisationsGraphListResponse {

    NodeValue organisation;
    ListValue awards;

    public ListValue getAwards() {
        return awards;
    }

    public OrganisationsGraphListResponse setAwards(ListValue awards) {
        this.awards = awards;
        return this;
    }

    public NodeValue getOrganisation() {
        return organisation;
    }

    public OrganisationsGraphListResponse setOrganisation(NodeValue organisation) {
        this.organisation = organisation;
        return this;
    }
}
