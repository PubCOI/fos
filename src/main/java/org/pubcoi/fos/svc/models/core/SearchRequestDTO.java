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

public class SearchRequestDTO {

    String q;
    String type;
    Boolean groupResults;

    public String getQ() {
        return q;
    }

    public SearchRequestDTO setQ(String q) {
        this.q = q;
        return this;
    }

    public String getType() {
        return type;
    }

    public SearchRequestDTO setType(String type) {
        this.type = type;
        return this;
    }

    public Boolean getGroupResults() {
        return groupResults;
    }

    public SearchRequestDTO setGroupResults(Boolean groupResults) {
        this.groupResults = groupResults;
        return this;
    }
}
