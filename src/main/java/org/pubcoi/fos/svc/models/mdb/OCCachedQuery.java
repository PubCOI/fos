package org.pubcoi.fos.svc.models.mdb;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.OffsetDateTime;

@Document(collection = "oc_cached_queries")
public class OCCachedQuery {

    @Id
    String id;
    OffsetDateTime requestDT;
    String queryURL;
    byte[] response;

    OCCachedQuery() {}

    public OCCachedQuery(String queryURL, byte[] wrapper) {
        // if the API token changes we wouldn't want to invalidate our lookups
        this.queryURL = redact(queryURL);
        this.id = getHash(this.queryURL);
        this.response = wrapper;
        this.requestDT = OffsetDateTime.now();
    }

    public static String redact(String queryURL) {
        return queryURL.replaceAll("(api_token=.[^&]+)", "api_token=redacted");
    }

    public static String getHash(String queryURL) {
        return DigestUtils.sha1Hex(queryURL);
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
}
