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

package org.pubcoi.fos.svc.models.dao;

import org.pubcoi.fos.svc.models.neo.nodes.ClientNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResolveClientDAO {
    private static final Logger logger = LoggerFactory.getLogger(ResolveClientDAO.class);

    String id;
    String name;
    Boolean isCanonical;
    String canonicalId;
    String postCode;

    public ResolveClientDAO() {
    }

    public ResolveClientDAO(ClientNode clientNode) {
        this.id = clientNode.getId();
        this.name = clientNode.getName();
        this.isCanonical = clientNode.getCanonical();
        this.postCode = clientNode.getPostCode();
        this.canonicalId = (null != clientNode.getParent() ? clientNode.getParent().getClient().getId() : null);
        if (this.isCanonical && null != clientNode.getParent()) {
            logger.error("ClientNode {} is marked as canonical but has a parent node", this.id);
        }

//        if (null != clientNode.getParent() && !clientNode.getParent().isEmpty()) {
//            Optional<ClientNode> canonicalNode = clientNode.getParent().stream()
//                    .filter(n -> n.getClient().getCanonical())
//                    .map(ClientParentClientLink::getClient)
//                    .findFirst();
//            if (canonicalNode.isPresent()) {
//                logger.debug("Found canonical node {} -> {}", this.id, canonicalNode.get().getId());
//                this.canonicalID = canonicalNode.get().getId();
//            }
//            else {
//                logger.debug("Unable to find a canonical node for {}", this.id);
//            }
//        }
    }

    public String getId() {
        return id;
    }

    public ResolveClientDAO setId(String id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public ResolveClientDAO setName(String name) {
        this.name = name;
        return this;
    }

    public Boolean getCanonical() {
        return isCanonical;
    }

    public String getCanonicalId() {
        return canonicalId;
    }

    public ResolveClientDAO setCanonical(Boolean canonical) {
        isCanonical = canonical;
        return this;
    }

    public ResolveClientDAO setCanonicalId(String canonicalId) {
        this.canonicalId = canonicalId;
        return this;
    }

    public String getPostCode() {
        return postCode;
    }

    public ResolveClientDAO setPostCode(String postCode) {
        this.postCode = postCode;
        return this;
    }
}
