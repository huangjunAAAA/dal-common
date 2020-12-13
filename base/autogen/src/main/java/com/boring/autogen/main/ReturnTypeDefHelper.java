package com.boring.autogen.main;

import com.boring.autogen.model.AutoGenConfig;
import com.boring.autogen.model.AutoReturnDef;
import com.boring.dal.config.DataAccessConfig;
import com.boring.dal.config.DataEntry;

import java.util.*;

public class ReturnTypeDefHelper {

    public List<AutoReturnDef> buildReturnTypeDefinitionfromClass(DataAccessConfig config, Class<?> tclass, AutoGenConfig.AGCfg autoGenConfig){
        List<AutoReturnDef> rlst=new ArrayList<>();
        List<DataEntry> delist = config.getClassRelatedListInfo(tclass);
        if(delist!=null) {
            for (Iterator<DataEntry> iterator = delist.iterator(); iterator.hasNext(); ) {
                DataEntry de = iterator.next();
                if(de.getValueProperties().size()>1){
                    AutoReturnDef ard=new AutoReturnDef();
                    ard.fields=new LinkedHashMap<>(de.getValueProperties());
                    ard.targetList=de.getName();
                    ard.pkgName=autoGenConfig.output.pkg+".construct";
                    ard.fileName="Ret4"+de.getName();
                    ard.className=ard.pkgName+"."+ard.fileName;
                    rlst.add(ard);
                }
            }
        }
        return rlst;
    }

    public HashMap<String,AutoReturnDef> buildReturnTypeDefs(DataAccessConfig config, List<Class> classList, AutoGenConfig.AGCfg autoGenConfig){
        HashMap<String, AutoReturnDef> rmap=new HashMap<>();
        for (Iterator<Class> iterator = classList.iterator(); iterator.hasNext(); ) {
            Class cls =  iterator.next();
            List<AutoReturnDef> ardlst = buildReturnTypeDefinitionfromClass(config, cls, autoGenConfig);
            for (Iterator<AutoReturnDef> autoReturnDefIterator = ardlst.iterator(); autoReturnDefIterator.hasNext(); ) {
                AutoReturnDef ard =  autoReturnDefIterator.next();
                rmap.put(ard.targetList,ard);
            }
        }
        return rmap;
    }
}
