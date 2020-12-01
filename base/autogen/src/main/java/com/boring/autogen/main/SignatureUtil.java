package com.boring.autogen.main;

import com.boring.autogen.model.AutoMethodDef;
import org.springframework.util.StringUtils;

import java.util.Iterator;
import java.util.Map;

public class SignatureUtil {

    public String signatureDef(AutoMethodDef methodDef){
        StringBuilder sb= new StringBuilder();
        sb.append(methodDef.returnClass).append(" ").append(methodDef.methodName);
        sb.append("(");
        int i=0;
        for (Iterator<Map.Entry<String, Class>> iterator = methodDef.params.entrySet().iterator(); iterator.hasNext();i++ ) {
            Map.Entry<String, Class> p =  iterator.next();
            String[] p1=p.getKey().split("\\.");
            sb.append(p.getValue().getTypeName()).append(" ").append(p1[p1.length-1]).append("_").append(i);
            if(iterator.hasNext())
                sb.append(",");
        }
        sb.append(")").append(" ").append("throws ");
        for (Iterator<String> iterator = methodDef.exceptions.iterator(); iterator.hasNext(); ) {
            String ex =  iterator.next();
            sb.append(ex);
            if(iterator.hasNext())
                sb.append(",");
        }
        sb.append(";");
        return sb.toString();

    }

    public String annotationDef(AutoMethodDef methodDef){
        if(StringUtils.isEmpty(methodDef.targetList))
            return "@RemoteAccess";
        StringBuilder ra=new StringBuilder("@RemoteAccess");
        ra.append("(targetList=\"").append(methodDef.targetList).append("\", remote=\"").append(methodDef.remote+"\"");
        if(methodDef.idx!=null){
            ra.append(", idx=").append(methodDef.idx);
        }
        ra.append(")");
        return ra.toString();
    }
}
