package org.pubcoi.fos.svc.services;

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
}


