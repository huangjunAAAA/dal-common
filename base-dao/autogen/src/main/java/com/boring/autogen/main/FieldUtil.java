package com.boring.autogen.main;

public class FieldUtil {
    public String declare(String fname,Class ftype){
        return "public "+ftype.getSimpleName()+" "+sanitizeFieldName(fname)+";";
    }
    public String assign(String fname,Class ftype,String val,int idx){
        return sanitizeFieldName(fname)+" = ("+ftype.getSimpleName()+")"+val+"["+idx+"]"+";";
    }

    public String sanitizeFieldName(String fn){
        return fn.replaceAll("\\.","_");
    }

}
