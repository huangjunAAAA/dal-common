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

    public String implDef(AutoMethodDef methodDef){
        if(StringUtils.isEmpty(methodDef.targetList))
            return "";
        StringBuilder impl=new StringBuilder(signatureDef(methodDef).replace(";","{"));
        impl.append("\n");
        impl.append("        return comprehensiveDao.").append(methodDef.remote).append("(");


        StringBuilder argstr=new StringBuilder("new Object[]{");
        int bEmbrace=-1;
        int i=0;
        switch (methodDef.remote) {
            case "getDataListMulti": {
                bEmbrace=methodDef.params.size()-3;
                for (Iterator<String> iterator = methodDef.params.keySet().iterator(); iterator.hasNext();i++ ) {
                    String p =  iterator.next();
                    String[] p1=p.split("\\.");
                    argstr.append(p1[p1.length-1]).append("_").append(i);
                    if(i!=bEmbrace) {
                        if (iterator.hasNext())
                            argstr.append(",");
                    }else
                        argstr.append("},");
                }
                break;
            }
            case "getDataListSingle": {
                bEmbrace=methodDef.params.size()-3;
                for (Iterator<String> iterator = methodDef.params.keySet().iterator(); iterator.hasNext();i++ ) {
                    String p =  iterator.next();
                    String[] p1=p.split("\\.");
                    argstr.append(p1[p1.length-1]).append("_").append(i);
                    if(i!=bEmbrace)
                        argstr.append(",");
                    else
                        argstr.append("},");
                }
                argstr.append(methodDef.idx==null?0:methodDef.idx);
                break;
            }
            case "getDataListEntity": {
                bEmbrace=methodDef.params.size()-4;
                for (Iterator<String> iterator = methodDef.params.keySet().iterator(); iterator.hasNext();i++ ) {
                    String p =  iterator.next();
                    String[] p1=p.split("\\.");
                    argstr.append(p1[p1.length-1]).append("_").append(i);
                    if(i!=bEmbrace) {
                        argstr.append(",");
                    }else
                        argstr.append("}").append(",");
                }
                argstr.append(methodDef.idx==null?0:methodDef.idx);
                break;
            }
            case "getDataMapSingle": {
                for (Iterator<String> iterator = methodDef.params.keySet().iterator(); iterator.hasNext();i++ ) {
                    String p =  iterator.next();
                    String[] p1=p.split("\\.");
                    argstr.append(p1[p1.length-1]).append("_").append(i);
                    if(iterator.hasNext())
                        argstr.append(",");
                }
                argstr.append("}").append(",").append(methodDef.idx);
                break;
            }
            default:
                for (Iterator<String> iterator = methodDef.params.keySet().iterator(); iterator.hasNext();i++ ) {
                    String p =  iterator.next();
                    String[] p1=p.split("\\.");
                    argstr.append(p1[p1.length-1]).append("_").append(i);
                    if(iterator.hasNext())
                        argstr.append(",");
                }
                argstr.append("}");
        }






        impl.append("\"").append(methodDef.targetList).append("\",").append(argstr).append(");\n    }");
        return impl.toString();
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
