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

package org.pubcoi.fos.svc.models.mdb;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.OffsetDateTime;

@Document(collection = "oc_cached_queries")
public class OCCachedQuery {

    static final String REDACTED = "redacted";

    @Id
    String id;
    OffsetDateTime requestDT;
    String queryURL;
    byte[] response;
    int responseCode = -1;

    OCCachedQuery() {
        this.requestDT = OffsetDateTime.now();
    }

    public OCCachedQuery(String queryURL, byte[] wrapper) {
        this(queryURL);
        this.response = wrapper;
    }

    public OCCachedQuery(String queryURL) {
        this();
        // if the API token changes we wouldn't want to invalidate our lookups
        this.queryURL = redact(queryURL);
        this.id = getHash(this.queryURL);
    }

    public static String redact(String queryURL) {
        return queryURL.replaceAll("(api_token=.[^&]+)", String.format("api_token=%s", REDACTED));
    }

    public static String getHash(String queryURL) {
        String queryStr = queryURL.contains(REDACTED) ? queryURL : redact(queryURL);
        return DigestUtils.sha1Hex(queryStr);
    }

    public OffsetDateTime getRequestDT() {
        return requestDT;
    }

    public OCCachedQuery setRequestDT(OffsetDateTime requestDT) {
        this.requestDT = requestDT;
        return this;
    }

    public String getId() {
        return id;
    }

    public OCCachedQuery setId(String id) {
        this.id = id;
        return this;
    }

    public byte[] getResponse() {
        return response;
    }

    public OCCachedQuery setResponse(byte[] response) {
        this.response = response;
        return this;
    }

    public String getQueryURL() {
        return queryURL;
    }

    public OCCachedQuery setQueryURL(String queryURL) {
        this.queryURL = queryURL;
        return this;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public OCCachedQuery setResponseCode(int responseCode) {
        this.responseCode = responseCode;
        return this;
    }
}
