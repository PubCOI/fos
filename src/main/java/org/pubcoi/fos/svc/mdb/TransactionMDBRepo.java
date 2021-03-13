package org.pubcoi.fos.svc.mdb;

import org.pubcoi.fos.svc.models.core.transactions.FosTransaction;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TransactionMDBRepo extends MongoRepository<FosTransaction, String> {
}
