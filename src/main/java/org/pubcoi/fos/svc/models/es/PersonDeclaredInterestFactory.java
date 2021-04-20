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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Component
public class PersonDeclaredInterestFactory {

    @Value("${pubcoi.fos.interests.datasets}")
    String datasetsList;

    Set<String> validDatasets = new HashSet<>();

    @PostConstruct
    public void setup() {
        validDatasets.addAll(Arrays.asList(datasetsList.split(",")));
    }

    public PersonDeclaredInterest create(MnisMemberType member, RegisterCategoryType category, RegisterRecordType.RegisterRecordItem item, String datasetName) {
        // check dataset is valid
        if (!validDatasets.contains(datasetName)) {
            throw new IllegalArgumentException("Dataset name is not valid");
        }
        return new PersonDeclaredInterest(member, category, item, datasetName);
    }

}
