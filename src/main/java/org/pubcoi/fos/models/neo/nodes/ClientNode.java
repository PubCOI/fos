package org.pubcoi.fos.models.neo.nodes;

import org.pubcoi.fos.models.cf.FullNotice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;
import org.springframework.util.DigestUtils;

import java.util.HashSet;
import java.util.Set;

@Node(primaryLabel = "client")
public class ClientNode {
    private static final Logger logger = LoggerFactory.getLogger(ClientNode.class);

    @Id
    String id;

    String postCode;

    String clientName;

    @Relationship("AKA")
    ClientNode client;

    // TRUE
    Boolean canonical = false;

    @Relationship("PUBLISHED")
    Set<NoticeNode> notices = new HashSet<>();

    public ClientNode() {
    }

    static String resolveIDStr(FullNotice notice) {
        String postCodeN = getNormalisedPostCode(notice);
        String clientNameN = getNormalisedClientName(notice.getNotice().getOrganisationName());
        return String.format("%s:%s", getNormalisedClientName(clientNameN), postCodeN);
    }

    public static String resolveID(FullNotice notice) {
        String idStr = resolveIDStr(notice);
        return DigestUtils.md5DigestAsHex(idStr.getBytes());
    }

    public ClientNode(FullNotice notice) {
        this.id = resolveID(notice);
        this.clientName = notice.getNotice().getOrganisationName();
        logger.debug("Adding client with normalised ID {} ({})", resolveIDStr(notice), this.id);
    }

    private static String getNormalisedClientName(String clientName) {
        if (null == clientName) return "";
        return clientName.toUpperCase().replaceAll("[^A-Z0-9]", "");
    }

    private static String getNormalisedPostCode(FullNotice notice) {
        String postCode1 = normalisePostCode(notice.getNotice().getPostcode());
        String postCode2 = normalisePostCode(notice.getNotice().getContactDetails().getPostcode());
        if (null == postCode1 && null == postCode2) {
            logger.warn("Unable to find post code for client with notice {}", notice.getId());
            return "";
        }
        if (null == postCode1) return postCode2;
        if (null == postCode2) return postCode1;
        if (!postCode1.equals(postCode2)) {
            logger.warn("Client postcodes for notice {} do not match, returning first", notice.getId());
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

    public String getClientName() {
        return clientName;
    }

    public ClientNode setClientName(String clientName) {
        this.clientName = clientName;
        return this;
    }

    public ClientNode getClient() {
        return client;
    }

    public ClientNode setClient(ClientNode client) {
        this.client = client;
        return this;
    }

    public Boolean getCanonical() {
        return canonical;
    }

    public ClientNode setCanonical(Boolean canonical) {
        this.canonical = canonical;
        return this;
    }

    public Set<NoticeNode> getNotices() {
        return notices;
    }

    public ClientNode setNotices(Set<NoticeNode> notices) {
        this.notices = notices;
        return this;
    }
}
