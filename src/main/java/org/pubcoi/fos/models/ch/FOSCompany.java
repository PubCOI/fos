package org.pubcoi.fos.models.ch;

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
    String reference;
    FOSReferenceTypeE referenceType;
    String referenceName;

    @NonNull
    OffsetDateTime created = Instant.now().atOffset(ZoneOffset.UTC);

    @Nullable
    OffsetDateTime updated;

    public FOSCompany() {}

    public FOSCompany(AwardDetailParentType.AwardDetail award) {
        if (award.getReferenceType() == org.pubcoi.fos.models.cf.ReferenceTypeE.COMPANIES_HOUSE) {
            this.referenceType = FOSReferenceTypeE.companies_house;
        } else {
            throw new FOSRuntimeException("Unable to resolve enum");
        }
        this.reference = award.getReference();
    }

    public FOSCompany(FOSReferenceTypeE referenceType, String reference) {
        this.referenceType = referenceType;
        this.reference = reference;
    }

    public String getReference() {
        return reference;
    }

    public FOSCompany setReference(String reference) {
        this.reference = reference;
        return this;
    }

    public String getReferenceName() {
        return referenceName;
    }

    public FOSCompany setReferenceName(String referenceName) {
        this.referenceName = referenceName;
        return this;
    }

    public FOSReferenceTypeE getReferenceType() {
        return referenceType;
    }

    public FOSCompany setReferenceType(FOSReferenceTypeE referenceType) {
        this.referenceType = referenceType;
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
                .append(referenceType, FOSCompany.referenceType)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new org.apache.commons.lang3.builder.HashCodeBuilder(17, 37)
                .append(reference)
                .append(referenceType)
                .toHashCode();
    }

    @Override
    public String toString() {
        return "FOSCompany{" +
                "reference='" + reference + '\'' +
                ", referenceTypeE=" + referenceType +
                ", referenceName='" + referenceName + '\'' +
                ", updated=" + created +
                '}';
    }
}
