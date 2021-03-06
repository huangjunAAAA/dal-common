package com.boring.autogen.main;

import com.boring.autogen.model.AutoGenConfig;
import com.boring.autogen.model.AutoInterfaceDef;
import com.boring.autogen.model.AutoMethodDef;
import com.boring.autogen.model.AutoReturnDef;
import com.boring.dal.config.Constants;
import com.boring.dal.config.DataAccessConfig;
import com.boring.dal.config.DataEntry;
import com.boring.dal.config.SQLVarInfo;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class InterfaceDefHelper {

    public AutoInterfaceDef buildInterfaceDefinitionfromClass(DataAccessConfig config, Class<?> tclass, AutoGenConfig.AGCfg autoGenConfig, HashMap<String, AutoReturnDef> rtmap){
        AutoInterfaceDef target=new AutoInterfaceDef();
        target.pkgName=autoGenConfig.output.pkg;
        target.fileName=tclass.getSimpleName()+autoGenConfig.ending;
        target.className=target.pkgName+"."+target.fileName;
        target.entityClass=tclass;

        Class idClass=config.getEntityIdField(tclass).getType();
        String idType=idClass.getName();
        target.idClass=idType;

        // get
        AutoMethodDef get=new AutoMethodDef();
        get.exceptions.add(Exception.class.getName());
        get.methodName="get";
        get.params.put("id",idClass);
        get.returnClass=tclass.getSimpleName();
        target.methods.add(get);

        // batch get
        AutoMethodDef batchget=new AutoMethodDef();
        batchget.exceptions.add(Exception.class.getName());
        batchget.methodName="batchGet";
        batchget.params.put("idList", List.class);
        batchget.returnClass="List<"+tclass.getSimpleName()+">";
        target.methods.add(batchget);

        // update
        AutoMethodDef update=new AutoMethodDef();
        update.exceptions.add(Exception.class.getName());
        update.methodName="update";
        update.params.put(tclass.getName().toLowerCase(),tclass);
        update.returnClass="void";
        target.methods.add(update);

        // save
        AutoMethodDef save=new AutoMethodDef();
        save.exceptions.add(Exception.class.getName());
        save.methodName="save";
        save.params.put(tclass.getName().toLowerCase(),tclass);
        save.returnClass=idType;
        target.methods.add(save);

        // batch save
        AutoMethodDef batchsave=new AutoMethodDef();
        batchsave.exceptions.add(Exception.class.getName());
        batchsave.methodName="batchSave";
        batchsave.params.put(tclass.getName().toLowerCase()+"List",List.class);
        batchsave.returnClass="List<"+idType+">";
        target.methods.add(batchsave);

        List<DataEntry> delist = config.getClassRelatedListInfo(tclass);
        if(delist!=null) {
            for (Iterator<DataEntry> iterator = delist.iterator(); iterator.hasNext(); ) {
                DataEntry de = iterator.next();
                if (de.getMode().equals(Constants.ITEM_MODE_LIST)) {
                    List<AutoMethodDef> mlist = handleListItem(de, rtmap);
                    target.methods.addAll(mlist);
                }

                if (de.getMode().equals(Constants.ITEM_MODE_MAP)) {
                    List<AutoMethodDef> mlist = handleMapItem(de, rtmap);
                    target.methods.addAll(mlist);
                }
            }
        }
        return target;
    }

    private List<AutoMethodDef> handleListItem(DataEntry de, HashMap<String, AutoReturnDef> rtmap){
        List<AutoMethodDef> ret=new ArrayList<>();
        // countList
        AutoMethodDef countList=new AutoMethodDef();
        LinkedList<SQLVarInfo> kplist = de.getKeyProperties();
        int i=0;
        for (i = 0; i < kplist.size(); i++) {
            SQLVarInfo var = kplist.get(i);
            String vName=var.getter.getName().replace("get","").toLowerCase()+i;
            countList.params.put(vName,var.getter.getReturnType());
        }
        countList.exceptions.add(Exception.class.getName());
        countList.methodName="count"+handleMethodName(de.getName());
        countList.returnClass="Integer";
        countList.remote="countDataList";
        countList.targetList=de.getName();
        ret.add(countList);

        // getDataListMulti
        AutoMethodDef getDataListMulti=new AutoMethodDef();
        getDataListMulti.params=new LinkedHashMap<>(countList.params);
        getDataListMulti.params.put("start",Integer.class);
        getDataListMulti.params.put("count",Integer.class);



        getDataListMulti.methodName="listAll"+handleMethodName(de.getName());
        getDataListMulti.exceptions.add(Exception.class.getName());
        getDataListMulti.remote="getDataListMulti";
        getDataListMulti.targetList=de.getName();
        if(de.getValueProperties().size()>1){
            AutoReturnDef rt = rtmap.get(de.getName());
            getDataListMulti.returnClass="List<"+rt.className+">";
            getDataListMulti.pType=rt.className;
            ret.add(getDataListMulti);
        }

        // getDataListSingle
        LinkedHashMap<String, Class> vplist = de.getValueProperties();
        i=0;
        for (Iterator<Map.Entry<String, Class>> iterator = vplist.entrySet().iterator(); iterator.hasNext(); i++) {
            Map.Entry<String, Class> val =  iterator.next();
            AutoMethodDef getDataMapSingle=new AutoMethodDef();
            getDataMapSingle.targetList=de.getName();
            getDataMapSingle.remote="getDataListSingle";
            getDataMapSingle.idx=i;
            getDataMapSingle.exceptions.add(Exception.class.getName());
            String[] p1=val.getKey().split("\\.");
            getDataMapSingle.methodName="list"+handleMethodName(de.getName())+"For"+p1[p1.length-1]+i;
            getDataMapSingle.returnClass="List<"+val.getValue().getTypeName()+">";
            getDataMapSingle.params=getDataListMulti.params;
            ret.add(getDataMapSingle);
        }


        // getDataListEntity
        AutoMethodDef getDataListEntity=new AutoMethodDef();
        getDataListEntity.params=new LinkedHashMap<>(getDataListMulti.params);
        getDataListEntity.params.put("clazz",Class.class);
        getDataListEntity.returnClass="List";
        getDataListEntity.methodName="listEntity"+handleMethodName(de.getName());
        getDataListEntity.exceptions.add(Exception.class.getName());
        getDataListEntity.remote="getDataListEntity";
        getDataListEntity.targetList=de.getName();

        ret.add(getDataListEntity);
        return ret;
    }

    private List<AutoMethodDef> handleMapItem(DataEntry de, HashMap<String, AutoReturnDef> rtmap){
        List<AutoMethodDef> ret=new ArrayList<>();
        // getDataMapMulti
        AutoMethodDef getDataMapMulti=new AutoMethodDef();
        LinkedList<SQLVarInfo> kplist = de.getKeyProperties();
        int i=0;
        for (i = 0; i < kplist.size(); i++) {
            SQLVarInfo var = kplist.get(i);
            String vName=var.getter.getName().replace("get","").toLowerCase()+i;
            getDataMapMulti.params.put(vName,var.getter.getReturnType());
        }

        getDataMapMulti.methodName="mapAll"+handleMethodName(de.getName());
        getDataMapMulti.exceptions.add(Exception.class.getName());
        getDataMapMulti.remote="getDataMapMulti";
        getDataMapMulti.targetList=de.getName();
        if(de.getValueProperties().size()>1){
            AutoReturnDef rt = rtmap.get(de.getName());
            getDataMapMulti.returnClass=rt.className;
            ret.add(getDataMapMulti);
        }

        // getDataMapSingle
        LinkedHashMap<String, Class> vplist = de.getValueProperties();
        i=0;
        for (Iterator<Map.Entry<String, Class>> iterator = vplist.entrySet().iterator(); iterator.hasNext(); i++) {
            Map.Entry<String, Class> val =  iterator.next();
            AutoMethodDef getDataMapSingle=new AutoMethodDef();
            getDataMapSingle.targetList=de.getName();
            getDataMapSingle.remote="getDataMapSingle";
            getDataMapSingle.idx=i;
            getDataMapSingle.exceptions.add(Exception.class.getName());
            String[] p1=val.getKey().split("\\.");
            getDataMapSingle.methodName="map"+handleMethodName(de.getName())+"For"+p1[p1.length-1]+i;
            getDataMapSingle.returnClass=val.getValue().getTypeName();
            getDataMapSingle.params=getDataMapMulti.params;
            ret.add(getDataMapSingle);
        }

        return ret;
    }

    private String handleMethodName(String m){
        return m;
//        return m.replaceAll("_","");
    }
}
