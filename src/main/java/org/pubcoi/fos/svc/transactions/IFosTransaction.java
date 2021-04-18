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
