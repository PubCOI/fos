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

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.pubcoi.cdm.cf.FullNotice;
import org.pubcoi.fos.svc.models.core.Constants;
import org.pubcoi.fos.svc.models.core.NodeReference;
import org.pubcoi.fos.svc.models.neo.relationships.ClientNoticeLink;
import org.pubcoi.fos.svc.models.neo.relationships.ClientParentClientLink;
import org.pubcoi.fos.svc.models.neo.relationships.ClientPersonLink;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;
import org.springframework.util.DigestUtils;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.pubcoi.fos.svc.services.Utils.normalise;

@Node(primaryLabel = "Client")
public class ClientNode implements FosEntity {
    private static final Logger logger = LoggerFactory.getLogger(ClientNode.class);

    @Id
    @GeneratedValue
    Long graphId;
    String fosId;
    String postCode;
    String name;
    boolean hidden = false;

    @Relationship(Constants.Neo4J.REL_AKA)
    ClientParentClientLink parent;

    boolean canonical = false;

    @Relationship(Constants.Neo4J.REL_PUBLISHED)
    List<ClientNoticeLink> notices = new ArrayList<>();

    @Relationship(Constants.Neo4J.REL_PERSON)
    List<ClientPersonLink> persons = new ArrayList<>();

    public ClientNode() {
    }

    public ClientNode(NodeReference node) {
        this.fosId = node.getFosId();
    }

    public ClientNode(FullNotice notice) {
        this.fosId = resolveId(notice);
        this.name = notice.getNotice().getOrganisationName();
        this.postCode = getNormalisedPostCode(notice);
        logger.debug("Adding client {} -> (id:{})", resolveIdStr(notice), this.fosId);
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

    public String getFosId() {
        return fosId;
    }

    public ClientNode setFosId(String fosId) {
        this.fosId = fosId;
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

    public Boolean isHidden() {
        return hidden;
    }

    public void addNotice(NoticeNode noticeNode, String noticeId, ZonedDateTime publishedZDT) {
        addNotice(new ClientNoticeLink(this, noticeNode, noticeId, publishedZDT));
    }

    @Override
    public ClientNode setHidden(Boolean hidden) {
        this.hidden = hidden;
        return this;
    }

    public Long getGraphId() {
        return graphId;
    }

    public ClientParentClientLink getParent() {
        return parent;
    }

    public ClientNode setParent(ClientParentClientLink parent) {
        this.parent = parent;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        ClientNode that = (ClientNode) o;

        return new EqualsBuilder()
                .append(graphId, that.graphId)
                .append(fosId, that.fosId)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(graphId)
                .append(fosId)
                .toHashCode();
    }

    @Override
    public String toString() {
        return "ClientNode{" +
                "graphId=" + graphId +
                ", fosId='" + fosId + '\'' +
                ", name='" + name + '\'' +
                ", canonical=" + canonical +
                '}';
    }

    public ClientNode setNotices(List<ClientNoticeLink> notices) {
        this.notices = notices;
        return this;
    }

    public ClientNode setPersons(List<ClientPersonLink> persons) {
        this.persons = persons;
        return this;
    }

    public ClientNode addPerson(ClientPersonLink personLink) {
        if (null == this.persons) this.persons = new ArrayList<>();
        this.persons.add(personLink);
        return this;
    }

    public ClientNode addNotice(ClientNoticeLink clientNoticeLink) {
        if (null == this.notices) this.notices = new ArrayList<>();
        this.notices.add(clientNoticeLink);
        return this;
    }

    public boolean isCanonical() {
        return canonical;
    }

    public ClientNode setCanonical(boolean canonical) {
        this.canonical = canonical;
        return this;
    }

    public List<ClientNoticeLink> getNotices() {
        return notices;
    }

    public List<ClientPersonLink> getPersons() {
        return persons;
    }
}
