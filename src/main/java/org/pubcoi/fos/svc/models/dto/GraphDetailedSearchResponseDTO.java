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

import org.pubcoi.fos.svc.models.queries.GraphFTSDetailedResponse;
import org.pubcoi.fos.svc.models.queries.GraphFTSResponse;

import java.util.ArrayList;
import java.util.List;

public class GraphDetailedSearchResponseDTO extends GraphSearchResponseDTO {

    List<String> details = new ArrayList<>();

    public GraphDetailedSearchResponseDTO(GraphFTSDetailedResponse response, NodeTypeEnum nodeTypeEnum) {
        super(response, nodeTypeEnum);
        for (Object o : response.getDetails().asList()) {
            if (o instanceof String) {
                details.add((String) o);
            }
        }
    }

    public GraphDetailedSearchResponseDTO(GraphFTSResponse response, NodeTypeEnum nodeTypeEnum) {
        super(response, nodeTypeEnum);
    }

    public List<String> getDetails() {
        return details;
    }

    public GraphDetailedSearchResponseDTO setDetails(List<String> details) {
        this.details = details;
        return this;
    }
}
