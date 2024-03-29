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

package org.pubcoi.fos.svc.services;

import org.pubcoi.fos.svc.exceptions.core.FosCoreRuntimeException;
import org.pubcoi.fos.svc.models.neo.nodes.OrganisationNode;
import org.pubcoi.fos.svc.models.neo.nodes.PersonNode;
import org.pubcoi.fos.svc.repos.gdb.jpa.OrganisationsGraphRepo;
import org.pubcoi.fos.svc.repos.gdb.jpa.PersonsGraphRepo;
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
        return personsGraphRepo.findByFosId(personId).orElseThrow(() -> new FosCoreRuntimeException(
                String.format("Unable to find person ID %s in graph", personId)
        ));
    }

    @Override
    public List<OrganisationNode> getOrgPersonLinks(String personId) {
        PersonNode personNode = getPersonGraphObject(personId);
        return orgGraphRepo.findAllByPerson(personNode.getFosId());
    }

    @Override
    public PersonNode save(PersonNode personNode) {
        return personsGraphRepo.save(personNode);
    }
}
