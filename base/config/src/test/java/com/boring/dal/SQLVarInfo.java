package com.boring.dal;

public class SQLVarInfo {
    public final EntityFieldInfo fieldInfo;
    public final boolean isCollection;

    public SQLVarInfo(EntityFieldInfo getter, boolean isCollection) {
        this.fieldInfo = getter;
        this.isCollection = isCollection;
    }
}
