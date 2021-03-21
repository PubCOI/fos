package org.pubcoi.fos.svc.models.dao;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.pubcoi.cdm.cf.FullNotice;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class NoticeNodeDAO {

    String id;
    String title;
    OffsetDateTime postedDT;
    String organisation;
    String description;
    Double valueLow;
    Double valueHigh;
    Set<AwardDAO> awards = new HashSet<>();

    public NoticeNodeDAO() {}

    public NoticeNodeDAO(FullNotice notice) {
        Objects.requireNonNull(notice.getNotice());
        this.id = notice.getId();
        this.title = notice.getNotice().getTitle();
        this.postedDT = notice.getCreatedDate();
        this.valueLow = notice.getNotice().getValueLow();
        this.valueHigh = notice.getNotice().getValueHigh();
        if (null != notice.getNotice()) {
            this.organisation = notice.getNotice().getOrganisationName();
        }
        if (null != notice.getNotice() && null != notice.getNotice().getTitle() && notice.getNotice().getTitle().length() > 10) {
            this.description = notice.getNotice().getTitle();
        }
        else if (null != notice.getNotice() && null != notice.getNotice().getDescription() && notice.getNotice().getDescription().length() > 10) {
            this.description = notice.getNotice().getDescription();
        }
        else if (null != notice.getNotice() && null != notice.getNotice().getCpvDescription() && notice.getNotice().getCpvDescription().length() > 10) {
            this.description = notice.getNotice().getCpvDescription();
        }
    }

    public String getId() {
        return id;
    }

    public NoticeNodeDAO setId(String id) {
        this.id = id;
        return this;
    }

    public OffsetDateTime getPostedDT() {
        return postedDT;
    }

    public NoticeNodeDAO setPostedDT(OffsetDateTime postedDT) {
        this.postedDT = postedDT;
        return this;
    }

    public String getOrganisation() {
        return organisation;
    }

    public NoticeNodeDAO setOrganisation(String organisation) {
        this.organisation = organisation;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public NoticeNodeDAO setDescription(String description) {
        this.description = description;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        NoticeNodeDAO that = (NoticeNodeDAO) o;

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

    public void addAward(AwardDAO awardDAO) {
        this.awards.add(awardDAO);
    }

    public Set<AwardDAO> getAwards() {
        return awards;
    }

    public NoticeNodeDAO setAwards(Set<AwardDAO> awards) {
        this.awards = awards;
        return this;
    }

    public Double getValueLow() {
        return valueLow;
    }

    public Double getValueHigh() {
        return valueHigh;
    }

    public String getTitle() {
        return title;
    }

    public NoticeNodeDAO setTitle(String title) {
        this.title = title;
        return this;
    }

    public NoticeNodeDAO setValueLow(Double valueLow) {
        this.valueLow = valueLow;
        return this;
    }

    public NoticeNodeDAO setValueHigh(Double valueHigh) {
        this.valueHigh = valueHigh;
        return this;
    }
}
