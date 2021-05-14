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

import org.apache.commons.codec.digest.DigestUtils;
import org.pubcoi.fos.svc.models.neo.nodes.OrganisationNode;
import org.pubcoi.fos.svc.models.queries.ResultAndScore;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "fos_tasks")
public class DRResolvePotentialCOITask extends DRTask {

    String linkedId;
    Float score;

    DRResolvePotentialCOITask() {
    }

    public DRResolvePotentialCOITask(OrganisationNode organisationNode, ResultAndScore result) {
        this.taskType = FosTaskType.resolve_potential_coi;
        this.id = DigestUtils.sha1Hex(String.format("%s_%s_%s", this.taskType.toString(), organisationNode.getFosId(), result.getId()));
        this.entity = organisationNode;
        this.linkedId = result.getId();
        this.score = result.getScore();
    }

    public String getLinkedId() {
        return linkedId;
    }

    public Float getScore() {
        return score;
    }
}
