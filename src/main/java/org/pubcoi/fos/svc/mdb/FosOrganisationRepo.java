package org.pubcoi.fos.svc.mdb;

import org.pubcoi.fos.svc.models.core.FosOrganisation;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface FosOrganisationRepo extends MongoRepository<FosOrganisation, String> {
}