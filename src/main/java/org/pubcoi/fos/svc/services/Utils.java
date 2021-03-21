package org.pubcoi.fos.svc.services;

public class Utils {
    public static String normalise(String name) {
        if (null == name) return "";
        return name.toUpperCase().replaceAll("[^A-Z0-9]", "");
    }
}


