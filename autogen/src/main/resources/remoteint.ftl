package ${idf.pkgName};

import java.util.*;
import com.boring.dal.remote.autorpc.AutoRpc;
import com.boring.dal.remote.autorpc.RemoteAccess;
import ${idf.entityClass.typeName};

@AutoRpc
public interface ${idf.fileName}{

<#list idf.methods as m>
    ${util.annotationDef(m)}
    ${util.signatureDef(m)}
</#list>


}