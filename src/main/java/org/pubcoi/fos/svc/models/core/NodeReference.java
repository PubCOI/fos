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

import org.pubcoi.fos.svc.models.neo.nodes.FosEntity;

public class NodeReference {
    String nodeType;
    String id;

    public NodeReference() {
    }

    public NodeReference(FosEntity node) {
        this.nodeType = node.getClass().getTypeName();
        this.id = node.getId();
    }

    public String getNodeType() {
        return nodeType;
    }

    public NodeReference setNodeType(String nodeType) {
        this.nodeType = nodeType;
        return this;
    }

    public String getId() {
        return id;
    }

    public NodeReference setId(String id) {
        this.id = id;
        return this;
    }
}
