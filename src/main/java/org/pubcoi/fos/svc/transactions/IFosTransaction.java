package org.pubcoi.fos.svc.transactions;

public interface IFosTransaction {

    /**
     * Execute the implementation code to add the entry to the database.
     * Note this method MUST be written in such a way that it can be run multiple times.
     */
    FosTransaction exec();

    /**
     * For reading transaction data back in
     * @param transaction the transaction object
     * @return IFosTransaction that can then be executed
     */
    IFosTransaction fromTransaction(FosTransaction transaction);

    /**
     * Used for storing the transaction details so they can be executed elsewhere
     * @return The transaction object
     */
    FosTransaction getTransaction();
}
