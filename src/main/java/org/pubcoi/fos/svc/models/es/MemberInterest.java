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

package org.pubcoi.fos.svc.models.es;

import org.apache.commons.codec.digest.DigestUtils;
import org.pubcoi.cdm.mnis.MnisInterestCategoryType;
import org.pubcoi.cdm.mnis.MnisInterestType;
import org.pubcoi.cdm.mnis.MnisMemberType;
import org.pubcoi.fos.svc.services.Utils;
import org.springframework.data.elasticsearch.annotations.*;
import org.springframework.data.neo4j.core.schema.Id;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Document(indexName = "members_interests")
@Setting(settingPath = "es/members_interests.json")
public class MemberInterest {

    @Id
    String id;
    @Field(type = FieldType.Keyword)
    String personNodeId;
    @Field(type = FieldType.Keyword)
    Integer mnisInterestId;
    @Field(type = FieldType.Keyword)
    String pwInterestId;
    @Field(type = FieldType.Keyword)
    String pwPersonId;
    @Field(type = FieldType.Keyword)
    Integer mnisPersonId;
    String personFullName;
    String text;
    @Field(type = FieldType.Date, format = DateFormat.date)
    LocalDate registeredDate;
    Integer pwCategory;
    @Field(type = FieldType.Keyword)
    String pwCategoryDescription;
    Integer mnisCategory;
    @Field(type = FieldType.Keyword)
    String mnisCategoryDescription;
    Boolean donation;
    @Field(type = FieldType.Text, analyzer = "names_analyzer")
    String donorName;
    Set<String> datasets = new HashSet<>();
    Float valueSum;
    SourceEnum source;

    MemberInterest() {
    }

    public MemberInterest(MnisMemberType member, MnisInterestCategoryType category, MnisInterestType interest) {
        this(member);
        this.source = SourceEnum.mnis;
        this.id = generateId(member, String.format("%s", interest.getId()));
        this.mnisInterestId = interest.getId();
        this.text = interest.getRegisteredInterest();
        this.registeredDate = LocalDate.from(interest.getCreatedDT());
        this.mnisCategory = category.getCategoryId();
        this.mnisCategoryDescription = category.getCategoryName();
    }

    public MemberInterest(MnisMemberType member, PWDeclaredInterest interest) {
        this(member);
        this.source = SourceEnum.pw;
        this.id = interest.getId();
        this.pwInterestId = interest.getId();
        this.text = interest.getText();
        this.registeredDate = interest.getRegistered();
        this.pwCategory = interest.getCategory();
        this.pwCategoryDescription = interest.getCategoryDescription();
        this.valueSum = interest.getValueSum();
        this.donation = interest.getDonation();
        this.donorName = interest.getDonorName();
    }

    private MemberInterest(MnisMemberType member) {
        this.personNodeId = Utils.mnisIdHash(member.getMemberId());
        this.pwPersonId = member.getPwId();
        this.mnisPersonId = member.getMemberId();
        this.personFullName = member.getFullTitle();
    }

    private String generateId(MnisMemberType member, String interestId) {
        return DigestUtils.sha1Hex(String.format("member_interest:%s:%s", member.getMemberId(), interestId));
    }

    public String getId() {
        return id;
    }

    public MemberInterest setId(String id) {
        this.id = id;
        return this;
    }

    public String getPersonNodeId() {
        return personNodeId;
    }

    public MemberInterest setPersonNodeId(String personNodeId) {
        this.personNodeId = personNodeId;
        return this;
    }

    public Integer getMnisInterestId() {
        return mnisInterestId;
    }

    public MemberInterest setMnisInterestId(Integer mnisInterestId) {
        this.mnisInterestId = mnisInterestId;
        return this;
    }

    public String getPwPersonId() {
        return pwPersonId;
    }

    public MemberInterest setPwPersonId(String pwPersonId) {
        this.pwPersonId = pwPersonId;
        return this;
    }

    public Integer getMnisPersonId() {
        return mnisPersonId;
    }

    public MemberInterest setMnisPersonId(Integer mnisPersonId) {
        this.mnisPersonId = mnisPersonId;
        return this;
    }

    public String getPersonFullName() {
        return personFullName;
    }

    public MemberInterest setPersonFullName(String personFullName) {
        this.personFullName = personFullName;
        return this;
    }

    public String getText() {
        return text;
    }

    public MemberInterest setText(String text) {
        this.text = text;
        return this;
    }

    public LocalDate getRegisteredDate() {
        return registeredDate;
    }

    public MemberInterest setRegisteredDate(LocalDate registeredDate) {
        this.registeredDate = registeredDate;
        return this;
    }

    public Integer getPwCategory() {
        return pwCategory;
    }

    public MemberInterest setPwCategory(Integer pwCategory) {
        this.pwCategory = pwCategory;
        return this;
    }

    public String getPwCategoryDescription() {
        return pwCategoryDescription;
    }

    public MemberInterest setPwCategoryDescription(String pwCategoryDescription) {
        this.pwCategoryDescription = pwCategoryDescription;
        return this;
    }

    public Integer getMnisCategory() {
        return mnisCategory;
    }

    public MemberInterest setMnisCategory(Integer mnisCategory) {
        this.mnisCategory = mnisCategory;
        return this;
    }

    public String getMnisCategoryDescription() {
        return mnisCategoryDescription;
    }

    public MemberInterest setMnisCategoryDescription(String mnisCategoryDescription) {
        this.mnisCategoryDescription = mnisCategoryDescription;
        return this;
    }

    public Boolean getDonation() {
        return donation;
    }

    public MemberInterest setDonation(Boolean donation) {
        this.donation = donation;
        return this;
    }

    public String getDonorName() {
        return donorName;
    }

    public MemberInterest setDonorName(String donorName) {
        this.donorName = donorName;
        return this;
    }

    public Set<String> getDatasets() {
        return datasets;
    }

    public MemberInterest setDatasets(Set<String> datasets) {
        this.datasets = datasets;
        return this;
    }

    public Float getValueSum() {
        return valueSum;
    }

    public MemberInterest setValueSum(Float valueSum) {
        this.valueSum = valueSum;
        return this;
    }

    public SourceEnum getSource() {
        return source;
    }

    public MemberInterest setSource(SourceEnum source) {
        this.source = source;
        return this;
    }

    public String getPwInterestId() {
        return pwInterestId;
    }

    public MemberInterest setPwInterestId(String pwInterestId) {
        this.pwInterestId = pwInterestId;
        return this;
    }
}
