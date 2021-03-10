package org.pubcoi.fos.svc.models.neo.nodes;

import org.pubcoi.fos.models.cf.FullNotice;
import org.pubcoi.fos.svc.models.neo.relationships.ClientNoticeLink;
import org.pubcoi.fos.svc.models.neo.relationships.ClientParentClientLink;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;
import org.springframework.util.DigestUtils;

import java.util.ArrayList;
import java.util.List;

@Node(primaryLabel = "Client")
public class ClientNode implements FOSEntity {
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
    List<ClientNoticeLink> tenders = new ArrayList<>();

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
        this.name = notice.getNotice().getOrganisationName();
        this.postCode = getNormalisedPostCode(notice);
        logger.debug("Adding client {} -> (id:{})", resolveIDStr(notice), this.id);
    }

    private static String getNormalisedClientName(String name) {
        if (null == name) return "";
        return name.toUpperCase().replaceAll("[^A-Z0-9]", "");
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
        tenders.add(new ClientNoticeLink(this, notice));
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

    public List<ClientNoticeLink> getTenders() {
        return tenders;
    }

    public ClientNode setTenders(List<ClientNoticeLink> tenders) {
        this.tenders = tenders;
        return this;
    }
}
