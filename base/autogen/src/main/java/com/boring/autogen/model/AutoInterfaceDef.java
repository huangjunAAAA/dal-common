package com.boring.autogen.model;

import java.util.ArrayList;
import java.util.List;

public class AutoInterfaceDef {
    public String fileName;
    public List<AutoMethodDef> methods=new ArrayList<>();
    public String className;
    public String pkgName;
    public Class entityClass;

    public String getFileName() {
        return fileName;
    }

    public List<AutoMethodDef> getMethods() {
        return methods;
    }

    public String getClassName() {
        return className;
    }

    public String getPkgName() {
        return pkgName;
    }

    public Class getEntityClass() {
        return entityClass;
    }
}
