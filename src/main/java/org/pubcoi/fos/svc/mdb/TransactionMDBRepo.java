package org.pubcoi.fos.svc.mdb;

import org.pubcoi.fos.svc.models.core.transactions.FOSTransaction;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TransactionMDBRepo extends MongoRepository<FOSTransaction, String> {
}
