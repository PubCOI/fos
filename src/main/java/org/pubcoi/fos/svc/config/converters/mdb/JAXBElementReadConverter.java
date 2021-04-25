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

package org.pubcoi.fos.svc.config.converters.mdb;

import com.mongodb.DBObject;
import org.bson.conversions.Bson;
import org.pubcoi.cdm.cf.FullNotice;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.mongodb.core.convert.MongoConverter;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import java.util.LinkedHashMap;
import java.util.List;

public class JAXBElementReadConverter implements Converter<DBObject, JAXBElement<FullNotice>> {
    MongoConverter converter;

    public JAXBElementReadConverter(MongoConverter converter) {
        this.converter = converter;
    }

    @Override
    public JAXBElement<FullNotice> convert(DBObject dbObject) {
        Class declaredType, scope;
        QName name = qNameFromString((String) dbObject.get("name"));
        Object rawValue = dbObject.get("value");
        try {
            declaredType = Class.forName((String) dbObject.get("declaredType"));
        } catch (ClassNotFoundException e) {
            if (rawValue.getClass().isArray()) declaredType = List.class;
            else declaredType = LinkedHashMap.class;
        }
        try {
            scope = Class.forName((String) dbObject.get("scope"));
        } catch (ClassNotFoundException e) {
            scope = JAXBElement.GlobalScope.class;
        }
        Object value = rawValue instanceof DBObject ? converter.read(declaredType, (Bson) rawValue) : rawValue;
        return new JAXBElement<FullNotice>(name, declaredType, scope, (FullNotice) value);
    }

    QName qNameFromString(String s) {
        String[] parts = s.split("[{}]");
        if (parts.length > 2) return new QName(parts[1], parts[2], parts[0]);
        if (parts.length == 1) return new QName(parts[0]);
        return new QName("undef");
    }
}
