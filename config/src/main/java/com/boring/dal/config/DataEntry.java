package com.boring.dal.config;

import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.LinkedList;

public class DataEntry {
    private String name;
    private String sql;
    private String mode;
    private String cache = Constants.CACHE_MODE_NONE;

    private LinkedList<SQLVarInfo> keyProperties;

    private LinkedList<Class> relatedClass;

    private LinkedHashMap<String, Class> valueProperties;

    private LinkedList<Method> orderByProperties;

    public LinkedHashMap<String, Class> getValueProperties() {
        return valueProperties;
    }

    public void setValueProperties(LinkedHashMap<String, Class> valueProperties) {
        this.valueProperties = valueProperties;
    }

    public LinkedList<SQLVarInfo> getKeyProperties() {
        return keyProperties;
    }

    public void setKeyProperties(LinkedList<SQLVarInfo> keyProperties) {
        this.keyProperties = keyProperties;
    }

    public LinkedList<Class> getRelatedClass() {
        return relatedClass;
    }

    public void setRelatedClass(LinkedList<Class> relatedClass) {
        this.relatedClass = relatedClass;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getCache() {
        return cache;
    }

    public void setCache(String cache) {
        this.cache = cache;
    }

    public LinkedList<Method> getOrderByProperties() {
        return orderByProperties;
    }

    public void setOrderByProperties(LinkedList<Method> orderByProperties) {
        this.orderByProperties = orderByProperties;
    }
}
