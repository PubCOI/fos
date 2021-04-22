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

import org.pubcoi.fos.svc.models.neo.nodes.ClientNode;

import java.util.HashSet;
import java.util.Set;

public class ClientNodeDTO {

    String id;
    String name;
    String postCode;
    Integer noticeCount;
    Set<NoticeNodeDTO> notices = new HashSet<>();

    public ClientNodeDTO() {}

    public ClientNodeDTO(ClientNode client) {
        this.id = client.getId();
        this.name = client.getName();
        this.postCode = client.getPostCode();
        this.noticeCount = client.getNoticeRelationships().size();
    }

    public String getName() {
        return name;
    }

    public ClientNodeDTO setName(String name) {
        this.name = name;
        return this;
    }

    public String getId() {
        return id;
    }

    public ClientNodeDTO setId(String id) {
        this.id = id;
        return this;
    }

    public String getPostCode() {
        return postCode;
    }

    public ClientNodeDTO setPostCode(String postCode) {
        this.postCode = postCode;
        return this;
    }

    public Integer getNoticeCount() {
        return noticeCount;
    }

    public ClientNodeDTO setNoticeCount(Integer noticeCount) {
        this.noticeCount = noticeCount;
        return this;
    }

    public Set<NoticeNodeDTO> getNotices() {
        return notices;
    }

    public ClientNodeDTO setNotices(Set<NoticeNodeDTO> notices) {
        this.notices = notices;
        return this;
    }
}
