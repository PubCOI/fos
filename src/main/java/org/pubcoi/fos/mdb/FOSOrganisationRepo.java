package org.pubcoi.fos.mdb;

import org.pubcoi.fos.models.core.FOSOrganisation;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface FOSOrganisationRepo extends MongoRepository<FOSOrganisation, String> {
}
