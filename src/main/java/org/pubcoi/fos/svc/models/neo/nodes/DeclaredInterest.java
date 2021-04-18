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

package org.pubcoi.fos.svc.models.neo.nodes;

import org.pubcoi.cdm.mnis.MnisInterestType;
import org.springframework.data.neo4j.core.schema.DynamicLabels;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

import java.util.HashSet;
import java.util.Set;

import static org.pubcoi.fos.svc.services.Utils.parliamentaryId;

@Node(primaryLabel = "Interest")
public class DeclaredInterest implements FosEntity {

    // for now we're just storing interests defined in parliamentary register
    // but in future we might also hold other declared interests here
    // and then link to the entities mentioned within blurb of declaration

    @Id
    String id;
    String text;
    String comments;
    @DynamicLabels
    Set<String> labels = new HashSet<>();
    Boolean hidden = false;

    DeclaredInterest() {}

    public DeclaredInterest(MnisInterestType interestType) {
        this.id = parliamentaryId(interestType.getId());
        this.text = interestType.getRegisteredInterest();
    }

    @Override
    public String getId() {
        return id;
    }

    public DeclaredInterest setId(String id) {
        this.id = id;
        return this;
    }

    @Override
    public Boolean getHidden() {
        return hidden;
    }

    @Override
    public FosEntity setHidden(Boolean hidden) {
        this.hidden = hidden;
        return this;
    }

    public String getText() {
        return text;
    }

    public DeclaredInterest setText(String text) {
        this.text = text;
        return this;
    }

    public String getComments() {
        return comments;
    }

    public DeclaredInterest setComments(String comments) {
        this.comments = comments;
        return this;
    }

    public Set<String> getLabels() {
        return labels;
    }

    public DeclaredInterest setLabels(Set<String> labels) {
        this.labels = labels;
        return this;
    }
}
