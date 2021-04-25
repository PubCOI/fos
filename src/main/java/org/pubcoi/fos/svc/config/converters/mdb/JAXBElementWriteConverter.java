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

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.mongodb.core.convert.MongoConverter;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

public class JAXBElementWriteConverter implements Converter<JAXBElement, DBObject> {
    MongoConverter converter;

    public JAXBElementWriteConverter(MongoConverter converter) {
        this.converter = converter;
    }

    @Override
    public DBObject convert(JAXBElement jaxbElement) {
        DBObject dbObject = new BasicDBObject();
        dbObject.put("name", qNameToString(jaxbElement.getName()));
        dbObject.put("declaredType", jaxbElement.getDeclaredType().getName());
        dbObject.put("scope", jaxbElement.getScope().getCanonicalName());
        dbObject.put("value", converter.convertToMongoType(jaxbElement.getValue()));
        dbObject.put("_class", JAXBElement.class.getName());
        return dbObject;
    }

    public String qNameToString(QName name) {
        if (name.getNamespaceURI().equals(XMLConstants.NULL_NS_URI)) return name.getLocalPart();
        return name.getPrefix() + '{' + name.getNamespaceURI() + '}' + name.getLocalPart();
    }
}