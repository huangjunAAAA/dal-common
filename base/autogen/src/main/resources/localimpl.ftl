package ${idf.pkgName};

import java.util.*;
import com.boring.dal.cache.ComprehensiveDao;
import ${idf.entityClass.typeName};
import javax.annotation.Resource;
import ${idf.pkgName}.construct.*;
import org.springframework.stereotype.Component;

@Component
public class ${idf.fileName}{

    @Resource
    private ComprehensiveDao comprehensiveDao;

    
    public ${idf.entityClass.typeName} get(${idf.idClass} id) throws Exception {
        return comprehensiveDao.get(id,${idf.entityClass.typeName}.class);
    }

    
    public List<${idf.entityClass.typeName}> batchGet(List objLst) throws Exception {
        return comprehensiveDao.batchGet(objLst,${idf.entityClass.typeName}.class);
    }

    
    public void update(${idf.entityClass.typeName} obj) throws Exception {
        comprehensiveDao.update(obj);
    }

    
    public ${idf.idClass} save(${idf.entityClass.typeName} obj) throws Exception {
        return (${idf.idClass}) comprehensiveDao.save(obj);
    }

    
    public List<${idf.idClass}> batchSave(List objLst) throws Exception {
        return batchSave(objLst);
    }
<#list idf.methods as m>
    ${util.implDef(m)}

</#list>
}