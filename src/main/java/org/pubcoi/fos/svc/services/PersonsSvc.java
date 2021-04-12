package org.pubcoi.fos.svc.services;

import org.pubcoi.fos.svc.models.neo.nodes.OrganisationNode;
import org.pubcoi.fos.svc.models.neo.nodes.PersonNode;

import java.util.List;

public interface PersonsSvc {
    PersonNode getPersonGraphObject(String personId);

    List<OrganisationNode> getOrgPersonLinks(String personId);
}
