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
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.pubcoi.cdm.mnis.MnisMemberType;
import org.pubcoi.cdm.pw.RegisterCategoryType;
import org.pubcoi.cdm.pw.RegisterRecordType;
import org.pubcoi.fos.svc.services.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.annotation.Id;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Not sure about this yet
 * The reason I'm holding this data elsewhere than on the graph is because we can
 * do more powerful aggregation etc on ES than in Neo4j ... also the text searching
 * is a bit more flexible this way
 */
public abstract class PersonDeclaredInterest implements DeclaredInterest {
    private static final Logger logger = LoggerFactory.getLogger(PersonDeclaredInterest.class);

    private static String genericDateRegex = "([0-9]{1,2} (?:jan(?:uary)?|feb(?:ruary)?|mar(?:ch)?|apr(?:il)?|may|jun(?:e)?|jul(?:y)?|aug(?:ust)?|sep(?:tember)?|oct(?:ober)?|nov(?:ember)?|dec(?:ember)?) [1-2][0-9]{3})";
    private static Pattern currencyExtractor = Pattern.compile("£((?:([0-9]+))?(?:,)?(?:([0-9]+))?(?:,)?(?:([0-9]+))?(?:,)?(?:([0-9]+))?(?:\\.)?(?:([0-9]+)))");
    private static Pattern registeredDatePattern = Pattern.compile(String.format("registered %s", genericDateRegex), Pattern.CASE_INSENSITIVE);
    private static Pattern updatedDatePattern = Pattern.compile(String.format("updated %s", genericDateRegex), Pattern.CASE_INSENSITIVE);
    private static Pattern donationPattern = Pattern.compile("name of donor: ", Pattern.CASE_INSENSITIVE);
    private static Pattern donorPattern = Pattern.compile("name of donor:(.+)(?:address)", Pattern.CASE_INSENSITIVE);
    private static DateTimeFormatter dtfLong = DateTimeFormatter.ofPattern("d MMMM yyyy");
    private static DateTimeFormatter dtfShort = DateTimeFormatter.ofPattern("d MMM yyyy");

    // ID will be a combination of interest_id + person_id
    @Id
    String id;
    // must match the PersonNode id
    String personId;
    // is the interest ID on MNIS
    Integer interestId;
    // if member interest: hash of text
    // if lord interest: hash of parl:lord:%d
    // must relate back to the Interest node ID
    String interestHashId;
    // if data is from publicwhip, they use a different person ID than the Member_Id
    // example: uk.org.publicwhip/person/123456
    String pwPersonId;
    Integer parliamentaryId;
    String fullTitle;
    String text;
    LocalDate registered;
    Date fromDate;
    Date toDate;
    Integer category;
    String categoryDescription;
    Boolean donation = false;
    String donorName;
    Set<String> datasets = new HashSet<>();
    Float valueMin;
    Float valueMax;
    Float valueSum;
    Set<String> flags = new HashSet<>();

    PersonDeclaredInterest() {
    }

    protected PersonDeclaredInterest(MnisMemberType member, RegisterCategoryType category, RegisterRecordType.RegisterRecordItem item, String datasetName) {
        String c14n = canonicalize(item.getValue());
        this.id = DigestUtils.sha1Hex(String.format("%s:%s:%s", member.getMemberId(), category.getCategoryType(), DigestUtils.sha1Hex(c14n)));
        this.personId = Utils.parliamentaryId(member.getMemberId());
        // if the entry moves to another category we don't necessarily care ... so just keep hash to member id + c14n
        this.interestHashId = DigestUtils.sha1Hex(String.format("%s:%s", member.getMemberId(), this.id));
        // note this is populated by an earlier call
        this.pwPersonId = member.getPwId();
        this.parliamentaryId = member.getMemberId();
        this.fullTitle = member.getFullTitle();
        this.text = item.getValue();
        this.category = category.getCategoryType();
        this.categoryDescription = category.getCategoryName();
        this.datasets.add(datasetName);
        analyseText();
    }

    public PersonDeclaredInterest analyseText() {
        Matcher valueMatcher = currencyExtractor.matcher(this.text);
        Boolean matched = false;
        Float lowest = null;
        Float highest = 0f;
        Float sum = 0f;
        while (valueMatcher.find()) {
            matched = true;
            String valueStr = valueMatcher.group(1);
            float currentValue = Float.parseFloat(valueStr.replaceAll("[^0-9.]", ""));
            if (currentValue > highest) {
                highest = currentValue;
            }
            if (null == lowest) {
                lowest = currentValue;
            }
            if (lowest > currentValue) {
                lowest = currentValue;
            }
            sum += currentValue;
        }
        if (matched) {
            this.valueMin = lowest;
            this.valueMax = highest;
            this.valueSum = sum;
        }
        this.registered = getDate(registeredDatePattern, this.text);
        Matcher donationMatcher = donationPattern.matcher(this.text);
        while (donationMatcher.find()) {
            this.donation = true;
        }
        Matcher donorMatcher = donorPattern.matcher(this.text);
        while (donorMatcher.find()) {
            this.donorName = donorMatcher.group(1).strip();
        }
        return this;
    }

    private LocalDate getDate(Pattern pattern, String inputText) {
        Matcher matcher = pattern.matcher(inputText);
        while (matcher.find()) {
            String dateStr = matcher.group(1);
            try {
                return LocalDate.parse(dateStr, dtfLong);
            } catch (DateTimeParseException e) {
                try {
                    return LocalDate.parse(dateStr, dtfShort);
                }
                catch (DateTimeParseException f) {
                    logger.error("Unable to parse date {}", dateStr);
                }
            }
        }
        return null;
    }

    private String canonicalize(String input) {
        return input.toUpperCase().replaceAll("[^A-Z0-9]", "");
    }

    // NOTES
    // if record contains one item and the content is "Nil" then no interests are declared
    // if text begins with "name of donor" (pw data) then mark as donation (needs to be taken from non-clean)

    public String getId() {
        return id;
    }

    public PersonDeclaredInterest setId(String id) {
        this.id = id;
        return this;
    }

    public String getPersonId() {
        return personId;
    }

    public PersonDeclaredInterest setPersonId(String personId) {
        this.personId = personId;
        return this;
    }

    public String getInterestHashId() {
        return interestHashId;
    }

    public PersonDeclaredInterest setInterestHashId(String interestHashId) {
        this.interestHashId = interestHashId;
        return this;
    }

    public String getText() {
        return text;
    }

    public PersonDeclaredInterest setText(String text) {
        this.text = text;
        return this;
    }

    public String getPwPersonId() {
        return pwPersonId;
    }

    public PersonDeclaredInterest setPwPersonId(String pwPersonId) {
        this.pwPersonId = pwPersonId;
        return this;
    }

    public Date getFromDate() {
        return fromDate;
    }

    public PersonDeclaredInterest setFromDate(Date fromDate) {
        this.fromDate = fromDate;
        return this;
    }

    public Date getToDate() {
        return toDate;
    }

    public PersonDeclaredInterest setToDate(Date toDate) {
        this.toDate = toDate;
        return this;
    }

    public Integer getCategory() {
        return category;
    }

    public PersonDeclaredInterest setCategory(Integer category) {
        this.category = category;
        return this;
    }

    public String getCategoryDescription() {
        return categoryDescription;
    }

    public PersonDeclaredInterest setCategoryDescription(String categoryDescription) {
        this.categoryDescription = categoryDescription;
        return this;
    }

    public Boolean getDonation() {
        return donation;
    }

    public PersonDeclaredInterest setDonation(Boolean donation) {
        this.donation = donation;
        return this;
    }

    public Float getValueMin() {
        return valueMin;
    }

    public PersonDeclaredInterest setValueMin(Float valueMin) {
        this.valueMin = valueMin;
        return this;
    }

    public Integer getInterestId() {
        return interestId;
    }

    public PersonDeclaredInterest setInterestId(Integer interestId) {
        this.interestId = interestId;
        return this;
    }

    public Set<String> getDatasets() {
        return datasets;
    }

    public PersonDeclaredInterest setDatasets(Set<String> datasets) {
        this.datasets = datasets;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        PersonDeclaredInterest that = (PersonDeclaredInterest) o;

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

    @Override
    public String toString() {
        return "PersonDeclaredInterest{" +
                "id='" + id + '\'' +
                ", personId='" + personId + '\'' +
                ", pwPersonId='" + pwPersonId + '\'' +
                '}';
    }

    public LocalDate getRegistered() {
        return registered;
    }

    public PersonDeclaredInterest setRegistered(LocalDate registered) {
        this.registered = registered;
        return this;
    }

    public Float getValueMax() {
        return valueMax;
    }

    public PersonDeclaredInterest setValueMax(Float valueMax) {
        this.valueMax = valueMax;
        return this;
    }

    public Float getValueSum() {
        return valueSum;
    }

    public PersonDeclaredInterest setValueSum(Float valueSum) {
        this.valueSum = valueSum;
        return this;
    }

    public Integer getParliamentaryId() {
        return parliamentaryId;
    }

    public PersonDeclaredInterest setParliamentaryId(Integer parliamentaryId) {
        this.parliamentaryId = parliamentaryId;
        return this;
    }

    public Set<String> getFlags() {
        return flags;
    }

    public PersonDeclaredInterest setFlags(Set<String> flags) {
        this.flags = flags;
        return this;
    }

    public String getFullTitle() {
        return fullTitle;
    }

    public PersonDeclaredInterest setFullTitle(String fullTitle) {
        this.fullTitle = fullTitle;
        return this;
    }

    public String getDonorName() {
        return donorName;
    }

    public PersonDeclaredInterest setDonorName(String donorName) {
        this.donorName = donorName;
        return this;
    }
}
