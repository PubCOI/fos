package org.pubcoi.fos.svc.models.dao;

import org.pubcoi.fos.svc.models.neo.nodes.ClientNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResolveClientDAO {
    private static final Logger logger = LoggerFactory.getLogger(ResolveClientDAO.class);

    String id;
    String name;
    Boolean isCanonical;
    String canonicalID;
    String postCode;

    public ResolveClientDAO() {
    }

    public ResolveClientDAO(ClientNode clientNode) {
        this.id = clientNode.getId();
        this.name = clientNode.getName();
        this.isCanonical = clientNode.getCanonical();
        this.postCode = clientNode.getPostCode();
        this.canonicalID = (null != clientNode.getParent() ? clientNode.getParent().getClient().getId() : null);
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

    public String getCanonicalID() {
        return canonicalID;
    }

    public ResolveClientDAO setCanonical(Boolean canonical) {
        isCanonical = canonical;
        return this;
    }

    public ResolveClientDAO setCanonicalID(String canonicalID) {
        this.canonicalID = canonicalID;
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
