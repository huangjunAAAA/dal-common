package com.boring.autogen.model;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class AutoMethodDef {
    public String methodName;
    public String returnClass;
    public String pType;
    public LinkedHashMap<String,Class> params=new LinkedHashMap<>();
    public List<String> exceptions=new ArrayList<>();

    public String targetList;
    public String remote;
    public Integer idx;

    public String getMethodName() {
        return methodName;
    }

    public String getReturnClass() {
        return returnClass;
    }

    public LinkedHashMap<String, Class> getParams() {
        return params;
    }

    public List<String> getExceptions() {
        return exceptions;
    }

    public String getTargetList() {
        return targetList;
    }

    public String getRemote() {
        return remote;
    }

    public Integer getIdx() {
        return idx;
    }
}
