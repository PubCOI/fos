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

import org.pubcoi.cdm.mnis.MnisMemberType;
import org.pubcoi.cdm.pw.RegisterCategoryType;
import org.pubcoi.cdm.pw.RegisterRecordType;
import org.springframework.data.elasticsearch.annotations.Document;

@Document(indexName = "pw_interests")
public class PWDeclaredInterestESType extends PWDeclaredInterest implements PWDeclaredInterestInterface {
    PWDeclaredInterestESType() {}
    protected PWDeclaredInterestESType(MnisMemberType member, RegisterCategoryType category, RegisterRecordType.RegisterRecordItem item, String datasetName) {
        super(member, category, item, datasetName);
    }

    public PWDeclaredInterestESType(PWDeclaredInterest type) {
        this.id = type.getId();
        this.personId = type.getPersonId();
        this.interestId = type.getInterestId();
        this.interestHashId = type.getInterestHashId();
        this.pwPersonId = type.getPwPersonId();
        this.mnisPersonId = type.getMnisPersonId();
        this.fullTitle = type.getFullTitle();
        this.text = type.getText();
        this.registered = type.getRegistered();
        this.fromDate = type.getFromDate();
        this.toDate = type.getToDate();
        this.category = type.getCategory();
        this.categoryDescription = type.getCategoryDescription();
        this.donation = type.getDonation();
        this.donorName = type.getDonorName();
        this.datasets.addAll(type.getDatasets());
        this.valueMin = type.getValueMin();
        this.valueMax = type.getValueMax();
        this.valueSum = type.getValueSum();
        this.flags.addAll(type.getFlags());
    }

    public static PWDeclaredInterestESType asESType(PWDeclaredInterest type) {
        return (type instanceof PWDeclaredInterestESType) ? (PWDeclaredInterestESType) type : new PWDeclaredInterestESType(type);
    }
}
