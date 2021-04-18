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

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.pubcoi.fos.svc.models.core.NodeReference;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.OffsetDateTime;
import java.util.UUID;

@Document("fos_transactions")
public class FosTransaction {

    @Id
    String id = UUID.randomUUID().toString();
    FosTransactionType transactionType;
    NodeReference source;
    NodeReference target;
    String uid;
    OffsetDateTime transactionDT = OffsetDateTime.now();
    String notes;
    String transactionImpl;
    String parentTransactionId;

    // should only be built via helpers
    FosTransaction() {}

    public String getId() {
        return id;
    }

    public FosTransaction setId(String id) {
        this.id = id;
        return this;
    }

    public FosTransactionType getTransactionType() {
        return transactionType;
    }

    public FosTransaction setTransactionType(FosTransactionType transactionType) {
        this.transactionType = transactionType;
        return this;
    }

    public NodeReference getSource() {
        return source;
    }

    public FosTransaction setSource(NodeReference source) {
        this.source = source;
        return this;
    }

    public NodeReference getTarget() {
        return target;
    }

    public FosTransaction setTarget(NodeReference target) {
        this.target = target;
        return this;
    }

    public OffsetDateTime getTransactionDT() {
        return transactionDT;
    }

    public FosTransaction setTransactionDT(OffsetDateTime transactionDT) {
        this.transactionDT = transactionDT;
        return this;
    }

    public String getNotes() {
        return notes;
    }

    public FosTransaction setNotes(String notes) {
        this.notes = notes;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        FosTransaction that = (FosTransaction) o;

        return new EqualsBuilder()
                .append(id, that.id)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .toHashCode();
    }

    public String getUid() {
        return uid;
    }

    public FosTransaction setUid(String uid) {
        this.uid = uid;
        return this;
    }

    public FosTransaction setTransactionImpl(Class<? extends IFosTransaction> transactionImpl) {
        this.transactionImpl = transactionImpl.getCanonicalName();
        return this;
    }

    public FosTransaction withMeta(FosTransaction metaTransaction) {
        this.parentTransactionId = metaTransaction.getId();
        this.uid = metaTransaction.getUid();
        return this;
    }

}
