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

package org.pubcoi.fos.svc.models.dao.fts;

import org.pubcoi.fos.svc.models.queries.GraphFTSResponse;

// todo not sure this is needed - if we're just using the generic name / id response
public class PersonNodeFTSDAOResponse {
    String id;
    String name;
    Float score;

    public PersonNodeFTSDAOResponse() {}

    public PersonNodeFTSDAOResponse(GraphFTSResponse r) {
        this.id = r.getId();
        this.name = r.getName();
        this.score = (null != r.getScore()) ? r.getScore().asFloat() : null;
    }

    public String getId() {
        return id;
    }

    public PersonNodeFTSDAOResponse setId(String id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public PersonNodeFTSDAOResponse setName(String name) {
        this.name = name;
        return this;
    }

    public Float getScore() {
        return score;
    }

    public PersonNodeFTSDAOResponse setScore(Float score) {
        this.score = score;
        return this;
    }
}
