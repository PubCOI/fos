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

import org.pubcoi.cdm.cf.FullNotice;
import org.pubcoi.fos.svc.models.core.NodeReference;
import org.pubcoi.fos.svc.models.neo.relationships.ClientNoticeLink;
import org.pubcoi.fos.svc.models.neo.relationships.ClientParentClientLink;
import org.pubcoi.fos.svc.models.neo.relationships.ClientPersonLink;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;
import org.springframework.util.DigestUtils;

import java.util.ArrayList;
import java.util.List;

import static org.pubcoi.fos.svc.services.Utils.normalise;

@Node(primaryLabel = "Client")
public class ClientNode implements FosEntity {
    private static final Logger logger = LoggerFactory.getLogger(ClientNode.class);

    @Id
    String id;

    String postCode;

    String name;

    Boolean hidden = false;

    @Relationship("AKA")
    ClientParentClientLink parent;

    // TRUE
    Boolean canonical = false;

    @Relationship("PUBLISHED")
    List<ClientNoticeLink> notices = new ArrayList<>();

    @Relationship("REL_PERSON")
    List<ClientPersonLink> persons = new ArrayList<>();

    public ClientNode() {
    }

    public ClientNode(NodeReference node) {
        this.id = node.getId();
    }

    static String resolveIdStr(FullNotice notice) {
        String postCodeN = getNormalisedPostCode(notice);
        String clientNameN = normalise(notice.getNotice().getOrganisationName());
        return String.format("%s:%s", normalise(clientNameN), postCodeN);
    }

    public static String resolveId(FullNotice notice) {
        // we want to be able to deterministically figure out the ID but it's
        // not clear whether name + PC are enough
        String idStr = resolveIdStr(notice);
        return DigestUtils.md5DigestAsHex(idStr.getBytes());
    }

    public ClientNode(FullNotice notice) {
        this.id = resolveId(notice);
        this.name = notice.getNotice().getOrganisationName();
        this.postCode = getNormalisedPostCode(notice);
        logger.debug("Adding client {} -> (id:{})", resolveIdStr(notice), this.id);
    }

    private static String getNormalisedPostCode(FullNotice notice) {
        String postCode1 = normalisePostCode(notice.getNotice().getPostcode());
        if (null == postCode1) {
            logger.warn("Unable to find post code for client with notice {}", notice.getId());
            return "";
        }
        return postCode1;
    }

    private static String normalisePostCode(String postcode) {
        if (null == postcode) return null;
        return postcode.toUpperCase().replaceAll("[^A-Z0-9]", "");
    }

    public String getId() {
        return id;
    }

    public ClientNode setId(String id) {
        this.id = id;
        return this;
    }

    public String getPostCode() {
        return postCode;
    }

    public ClientNode setPostCode(String postCode) {
        this.postCode = postCode;
        return this;
    }

    public String getName() {
        return name;
    }

    public ClientNode setName(String name) {
        this.name = name;
        return this;
    }

    public Boolean getCanonical() {
        return canonical;
    }

    public ClientNode setCanonical(Boolean canonical) {
        this.canonical = canonical;
        return this;
    }

    public void addNotice(FullNotice notice) {
        notices.add(new ClientNoticeLink(this, notice));
    }

    public Boolean getHidden() {
        return hidden;
    }

    @Override
    public ClientNode setHidden(Boolean hidden) {
        this.hidden = hidden;
        return this;
    }

    public ClientParentClientLink getParent() {
        return parent;
    }

    public ClientNode setParent(ClientParentClientLink parent) {
        this.parent = parent;
        return this;
    }

    public List<ClientNoticeLink> getNoticeRelationships() {
        return notices;
    }

    public ClientNode setNotices(List<ClientNoticeLink> notices) {
        this.notices = notices;
        return this;
    }

    @Override
    public String toString() {
        return "ClientNode{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", canonical=" + canonical +
                '}';
    }

    public List<ClientNoticeLink> getNotices() {
        return notices;
    }

    public List<ClientPersonLink> getPersons() {
        return persons;
    }
}
