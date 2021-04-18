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

package org.pubcoi.fos.svc.services;

import org.apache.commons.codec.digest.DigestUtils;
import org.pubcoi.fos.svc.exceptions.FosRuntimeException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {

    public static Pattern ocCompanyPattern = Pattern.compile("oc_company:(.[a-z0-9]{1,3}):(.[A-Z0-9]{1,10})");

    public static String normalise(String name) {
        if (null == name) return "";
        return name.toUpperCase().replaceAll("[^A-Z0-9]", "");
    }

    public static String convertOCCompanyToGraphID(String objectId) {
        if (!objectId.startsWith("oc_company")) {
            throw new FosRuntimeException("Object ID must start with oc_company");
        }
        Matcher m = ocCompanyPattern.matcher(objectId);
        if (!m.matches()) throw new FosRuntimeException("Object ID " + objectId + " does not match expected pattern");
        return (String.format("%s:%s", m.group(1), m.group(2)));
    }

    public static String parliamentaryId(Integer id) {
        return DigestUtils.sha1Hex(String.format("parliament:%d", id));
    }
}


