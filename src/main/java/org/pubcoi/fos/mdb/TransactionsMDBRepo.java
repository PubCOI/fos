package org.pubcoi.fos.mdb;

import org.pubcoi.fos.models.core.transactions.FOSTransaction;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TransactionsMDBRepo extends MongoRepository<FOSTransaction, String> {
}
