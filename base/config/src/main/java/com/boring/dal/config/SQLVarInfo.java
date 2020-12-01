package com.boring.dal.config;

import java.lang.reflect.Method;

public class SQLVarInfo {
    public final Method getter;
    public final boolean isCollection;

    public SQLVarInfo(Method getter, boolean isCollection) {
        this.getter = getter;
        this.isCollection = isCollection;
    }
}
