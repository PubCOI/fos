package org.pubcoi.fos.svc.mdb;

import org.pubcoi.fos.svc.models.core.FOSOrganisation;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface FOSOrganisationRepo extends MongoRepository<FOSOrganisation, String> {
}
