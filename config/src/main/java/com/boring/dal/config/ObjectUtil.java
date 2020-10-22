package com.boring.dal.config;

public class ObjectUtil {
    public static String getGetterName(String fieldName) {
        return "get" + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
    }
}
