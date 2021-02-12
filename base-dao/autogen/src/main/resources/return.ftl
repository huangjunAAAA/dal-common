package ${ard.pkgName};
import java.util.*;

public class ${ard.fileName}{

<#list ard.fields as fname,ftype>
    ${util.declare(fname,ftype)}
</#list>
    public static ${ard.fileName} fromObjectArray(Object[] oay){
        ${ard.fileName} ret = new ${ard.fileName}();
    <#list ard.fields as fname,ftype>
        ret.${util.assign(fname,ftype,"oay",fname?index)}
    </#list>
        return ret;
    }

    public static List<${ard.fileName}> fromObjectArrayList(List<Object[]> oaylist){
        List<${ard.fileName}> rlst=new ArrayList<>();
        for(int i=0; i<oaylist.size(); ++i){
            ${ard.fileName} ro= fromObjectArray(oaylist.get(i));
            rlst.add(ro);
        }
        return rlst;
    }
}