package org.pubcoi.fos.models.core;

import com.mongodb.lang.NonNull;
import com.mongodb.lang.Nullable;
import org.pubcoi.fos.exceptions.FOSRuntimeException;
import org.pubcoi.fos.models.cf.AwardDetailParentType;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Document(collection = "fos_companies")
public class FOSCompany {

    @MongoId
    String id;
    String reference;
    DataSources source;

    @NonNull
    OffsetDateTime created = Instant.now().atOffset(ZoneOffset.UTC);

    @Nullable
    OffsetDateTime updated;

    public FOSCompany() {}

    public FOSCompany(AwardDetailParentType.AwardDetail award) {
        if (award.getReferenceType() == org.pubcoi.fos.models.cf.ReferenceTypeE.COMPANIES_HOUSE) {
            this.source = DataSources.oc_company;
        } else {
            throw new FOSRuntimeException("Unable to resolve source type");
        }
        this.reference = award.getReference();
        this.id = String.format("%s:%s", this.source, this.reference);
    }

    public FOSCompany(DataSources source, String reference) {
        this.source = source;
        this.reference = reference;
    }

    public String getReference() {
        return reference;
    }

    public FOSCompany setReference(String reference) {
        this.reference = reference;
        return this;
    }

    public DataSources getSource() {
        return source;
    }

    public FOSCompany setSource(DataSources source) {
        this.source = source;
        return this;
    }

    public OffsetDateTime getCreated() {
        return created;
    }

    public FOSCompany setCreated(OffsetDateTime created) {
        this.created = created;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        FOSCompany FOSCompany = (FOSCompany) o;

        return new org.apache.commons.lang3.builder.EqualsBuilder()
                .append(reference, FOSCompany.reference)
                .append(source, FOSCompany.source)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new org.apache.commons.lang3.builder.HashCodeBuilder(17, 37)
                .append(reference)
                .append(source)
                .toHashCode();
    }

    @Override
    public String toString() {
        return "FOSCompany{" +
                "reference='" + reference + '\'' +
                ", referenceTypeE=" + source +
                ", updated=" + created +
                '}';
    }

    public String getId() {
        return id;
    }

    public FOSCompany setId(String id) {
        this.id = id;
        return this;
    }

    @Nullable
    public OffsetDateTime getUpdated() {
        return updated;
    }

    public FOSCompany setUpdated(@Nullable OffsetDateTime updated) {
        this.updated = updated;
        return this;
    }
}

