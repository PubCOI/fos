package org.pubcoi.fos.svc.services;

import org.pubcoi.fos.svc.exceptions.FosRuntimeException;
import org.pubcoi.fos.svc.repos.gdb.OrganisationsGraphRepo;
import org.pubcoi.fos.svc.repos.gdb.PersonsGraphRepo;
import org.pubcoi.fos.svc.models.neo.nodes.OrganisationNode;
import org.pubcoi.fos.svc.models.neo.nodes.PersonNode;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PersonsSvcImpl implements PersonsSvc {

    final PersonsGraphRepo personsGraphRepo;
    final OrganisationsGraphRepo orgGraphRepo;

    public PersonsSvcImpl(PersonsGraphRepo personsGraphRepo, OrganisationsGraphRepo orgGraphRepo) {
        this.personsGraphRepo = personsGraphRepo;
        this.orgGraphRepo = orgGraphRepo;
    }

    @Override
    public PersonNode getPersonGraphObject(String personId) {
        return personsGraphRepo.findById(personId).orElseThrow(() -> new FosRuntimeException(
                String.format("Unable to find person ID %s in graph", personId)
        ));
    }

    @Override
    public List<OrganisationNode> getOrgPersonLinks(String personId) {
        PersonNode personNode = getPersonGraphObject(personId);
        return orgGraphRepo.findAllByPerson(personNode.getId());
    }

    @Override
    public PersonNode save(PersonNode personNode) {
        return personsGraphRepo.save(personNode);
    }
}
