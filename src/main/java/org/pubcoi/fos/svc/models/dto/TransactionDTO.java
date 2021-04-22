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

package org.pubcoi.fos.svc.models.dto;

import org.pubcoi.fos.svc.models.core.NodeReference;
import org.pubcoi.fos.svc.transactions.FosTransaction;
import org.pubcoi.fos.svc.transactions.FosTransactionType;

import java.time.OffsetDateTime;

public class TransactionDTO {

    String id;
    FosTransactionType transactionType;
    NodeReference source;
    NodeReference target;
    OffsetDateTime transactionDT;

    public TransactionDTO(FosTransaction transaction) {
        this.id = transaction.getId();
        this.transactionType = transaction.getTransactionType();
        this.source = transaction.getSource();
        this.target = transaction.getTarget();
        this.transactionDT = transaction.getTransactionDT();
    }

    public String getId() {
        return id;
    }

    public NodeReference getSource() {
        return source;
    }

    public NodeReference getTarget() {
        return target;
    }

    public OffsetDateTime getTransactionDT() {
        return transactionDT;
    }

    public FosTransactionType getTransactionType() {
        return transactionType;
    }
}
