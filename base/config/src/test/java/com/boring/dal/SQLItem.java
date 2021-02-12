package com.boring.dal;



import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.LinkedList;

public class SQLItem {
    private String sql;

    private LinkedList<SQLVarInfo> keyProperties;

    private LinkedList<EntityInfo> relatedClass;

    private LinkedHashMap<String, Class> valueProperties;

    private LinkedList<EntityFieldInfo> orderByProperties;

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

    public LinkedList<EntityInfo> getRelatedClass() {
        return relatedClass;
    }

    public void setRelatedClass(LinkedList<EntityInfo> relatedClass) {
        this.relatedClass = relatedClass;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public LinkedList<EntityFieldInfo> getOrderByProperties() {
        return orderByProperties;
    }

    public void setOrderByProperties(LinkedList<EntityFieldInfo> orderByProperties) {
        this.orderByProperties = orderByProperties;
    }
}
