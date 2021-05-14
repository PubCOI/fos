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

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.pubcoi.fos.svc.models.neo.nodes.ClientNode;
import org.pubcoi.fos.svc.models.neo.relationships.ClientNoticeLink;

import java.time.ZonedDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ClientsGraphListResponseDTO {

    String fosId;
    String name;
    int total = 0;
    boolean canonical = false;
    boolean hidden = false;
    ZonedDateTime firstNotice;
    ZonedDateTime lastNotice;
    List<String> notices;

    ClientsGraphListResponseDTO() {
    }

    public ClientsGraphListResponseDTO(ClientNode node) {
        this.fosId = node.getFosId();
        this.name = node.getName();
        this.total = null == node.getNotices() ? 0 : node.getNotices().size();
        this.canonical = node.isCanonical();
        this.hidden = node.isHidden();
        List<ClientNoticeLink> sorted = node.getNotices().stream().sorted(Comparator.comparing(ClientNoticeLink::getPublished)).collect(Collectors.toList());
        this.lastNotice = sorted.get(sorted.size() - 1).getPublished();
        this.firstNotice = sorted.get(0).getPublished();
        this.notices = sorted.stream().map(ClientNoticeLink::getFosId).collect(Collectors.toList());
    }

    public String getFosId() {
        return fosId;
    }

    public ClientsGraphListResponseDTO setFosId(String fosId) {
        this.fosId = fosId;
        return this;
    }

    public String getName() {
        return name;
    }

    public ClientsGraphListResponseDTO setName(String name) {
        this.name = name;
        return this;
    }

    public boolean isCanonical() {
        return canonical;
    }

    public ClientsGraphListResponseDTO setCanonical(boolean canonical) {
        this.canonical = canonical;
        return this;
    }

    public boolean isHidden() {
        return hidden;
    }

    public ClientsGraphListResponseDTO setHidden(boolean hidden) {
        this.hidden = hidden;
        return this;
    }

    public ZonedDateTime getFirstNotice() {
        return firstNotice;
    }

    public ClientsGraphListResponseDTO setFirstNotice(ZonedDateTime firstNotice) {
        this.firstNotice = firstNotice;
        return this;
    }

    public ZonedDateTime getLastNotice() {
        return lastNotice;
    }

    public ClientsGraphListResponseDTO setLastNotice(ZonedDateTime lastNotice) {
        this.lastNotice = lastNotice;
        return this;
    }

    public int getTotal() {
        return total;
    }

    public ClientsGraphListResponseDTO setTotal(int total) {
        this.total = total;
        return this;
    }

    public List<String> getNotices() {
        return notices;
    }

    public ClientsGraphListResponseDTO setNotices(List<String> notices) {
        this.notices = notices;
        return this;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("fosId", fosId)
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        ClientsGraphListResponseDTO that = (ClientsGraphListResponseDTO) o;

        return new EqualsBuilder()
                .append(fosId, that.fosId)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(fosId)
                .toHashCode();
    }
}
