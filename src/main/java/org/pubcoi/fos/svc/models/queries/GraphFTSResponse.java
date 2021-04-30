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

import org.neo4j.driver.internal.value.FloatValue;
import org.pubcoi.fos.svc.models.neo.nodes.FosEntity;

public class GraphFTSResponse {
    FosEntity entity;
    String fosId;
    String name;
    FloatValue score;

    public FloatValue getScore() {
        return score;
    }

    public GraphFTSResponse setScore(FloatValue score) {
        this.score = score;
        return this;
    }

    public String getFosId() {
        return fosId;
    }

    public GraphFTSResponse setFosId(String fosId) {
        this.fosId = fosId;
        return this;
    }

    public String getName() {
        return name;
    }

    public GraphFTSResponse setName(String name) {
        this.name = name;
        return this;
    }

    public FosEntity getEntity() {
        return entity;
    }

    public GraphFTSResponse setEntity(FosEntity entity) {
        this.entity = entity;
        return this;
    }
}
