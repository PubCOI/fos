package org.pubcoi.fos.svc.repos.mdb;

import org.pubcoi.fos.svc.models.core.FosOrganisation;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface OrganisationsMDBRepo extends MongoRepository<FosOrganisation, String> {
}
