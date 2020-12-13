package com.boring.autogen.model;

import java.util.LinkedHashMap;

public class AutoReturnDef {
    public String fileName;
    public String className;
    public String pkgName;
    public String targetList;
    public LinkedHashMap<String,Class> fields=new LinkedHashMap<>();

    public String getFileName() {
        return fileName;
    }

    public String getClassName() {
        return className;
    }

    public String getPkgName() {
        return pkgName;
    }

    public String getTargetList() {
        return targetList;
    }

    public LinkedHashMap<String, Class> getFields() {
        return fields;
    }
}
