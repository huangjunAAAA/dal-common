package com.boring.dal.config;

import com.boring.dal.YamlFileLoader;
import com.boring.dal.config.aap.AnnotationAssist;
import com.boring.dal.config.aap.JPAAssist;
import com.boring.dal.config.sqllinker.DruidSQLLinker;
import com.boring.dal.config.sqllinker.FieldSQLConnector;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.reflections8.Reflections;
import org.reflections8.scanners.SubTypesScanner;
import org.reflections8.scanners.TypeAnnotationsScanner;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;


import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;


public class DataAccessConfig implements ApplicationContextAware {

    AnnotationAssist annotationAssist=new JPAAssist();
	private ApplicationContext beanCtx;

	protected DataAccessConfig() {
    }

    public DataAccessConfig(DataAccessConfigFile raw,FieldSQLConnector linker){
    	this.linker=linker;
        this.raw=raw;
        init();
    }

    public DataAccessConfig(String configFile,FieldSQLConnector linker){
        this.configFile=configFile;
		this.linker=linker;
        DataAccessConfigFile dFile = YamlFileLoader.loadConfigFromPath(configFile, DataAccessConfigFile.class);
        if (dFile != null) {
            this.raw=dFile;
            init();
        }
    }

    private static final Logger logger = LogManager.getLogger("DAO");

    protected volatile DataAccessConfigFile raw;

    private volatile Map<Class, List<DataEntry>> associatedDataEntries;

    private Map<String, DataEntry> dataEntryMap;

    private FieldSQLConnector linker;

    private volatile LinkedHashMap<Class, AccessibleFieldInfo> ldClass;

    private String configFile;

    private Field getIdField(Class<?> cls) {
        Class<? extends Annotation> idAnno = annotationAssist.getEntityIdAnnotation();
        Field[] fields = cls.getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            Field f = fields[i];
            if (f.getAnnotation(idAnno) != null)
                return f;
        }
        return null;
    }

    public int getCacheDirtyLast() {
        return raw.cache.getDirty();
    }

    public int getSlaveDirtyLast() {
        return raw.cache.getPanic();
    }

    public void init() {
        if (raw == null)
            return;
        if (associatedDataEntries == null) {
            synchronized (this) {
                if (associatedDataEntries == null) {
                    loadAllClazz();
                    associatedDataEntries = new HashMap<>();
                    dataEntryMap = new HashMap<>();
                    for (Iterator<DataEntry> iterator = raw.getData().iterator(); iterator.hasNext(); ) {
                        DataEntry de = iterator.next();
                        parseDataEntry(de);
                    }
                }
            }
        }
    }

    public void addDataEntry(DataEntry de,List<Class> mappingClazz){
        init();
        addClazzToMaps(mappingClazz);
        parseDataEntry(de);
    }

    public List<DataEntry> getClassRelatedListInfo(Class cls) {
        init();
        return associatedDataEntries.get(cls);
    }

    private void parseDataEntry(DataEntry de) {
        linker.linkDataEntry(de, new ArrayList<>(ldClass.keySet()));
        logger.info("Complete parsing data entry:" + de.getName() + ", related class:" + de.getRelatedClass());
        for (Iterator<Class> classIterator = de.getRelatedClass().iterator(); classIterator.hasNext(); ) {
            Class acls = classIterator.next();
            List<DataEntry> lst = associatedDataEntries.get(acls);
            if (lst == null) {
                lst = new ArrayList<>();
                associatedDataEntries.put(acls, lst);
            }
            if (lst.indexOf(de) == -1)
                lst.add(de);
            dataEntryMap.put(de.getName(), de);
        }
        if (Constants.CACHE_MODE_AUTO.equals(de.getCache())) {
            de.setCache(Constants.CACHE_MODE_REDIS);
        }
    }

    public List<Class> loadAllClazz() {
        if (raw == null)
            return null;
        if (ldClass == null)
            synchronized (this) {
                if (ldClass == null) {
                    List<Class> toLoadClazz = loadConfiguredClass();
                    ldClass = new LinkedHashMap<>();
                    addClazzToMaps(toLoadClazz);
                }
            }
        return new ArrayList<>(ldClass.keySet());
    }

    private synchronized void addClazzToMaps(List<Class> toLoadClazz){
        for (Iterator<Class> iterator = toLoadClazz.iterator(); iterator.hasNext(); ) {
            Class cls = iterator.next();
            if(ldClass.containsKey(cls))
            	continue;
            Field idField = getIdField(cls);
            if (idField != null) {
                String gName = ObjectUtil.getGetterName(idField.getName());
                try {
                    Method m = cls.getMethod(gName);
                    ldClass.put(cls, new AccessibleFieldInfo(idField, m));
                } catch (NoSuchMethodException e) {
                    logger.error(e, e);
                }
            }
        }
    }

    protected List<Class> loadConfiguredClass(){
        ArrayList<Class> toLoadClazz = new ArrayList<>();
        ScannedModel dm = raw.getObjects();
        Reflections reflections = new Reflections(dm.getScanPkg(), new TypeAnnotationsScanner(), new SubTypesScanner());
        Class<? extends Annotation> entityAnno = annotationAssist.getEntityAnnotation();
        Set<Class<?>> tmp = reflections.getTypesAnnotatedWith(entityAnno);
        toLoadClazz.addAll(tmp);

        if (dm.getDefined() != null) {
            for (Iterator<String> iterator = dm.getDefined().iterator(); iterator.hasNext(); ) {
                String clsname = iterator.next();
                try {
                    Class<?> cls = Class.forName(clsname);
                    if (cls.getAnnotation(entityAnno) != null) {
                        if (toLoadClazz.indexOf(cls) == -1) {
                            toLoadClazz.add(cls);
                        }
                    }
                } catch (ClassNotFoundException e) {
                    logger.error(e, e);
                }
            }
        }
        return toLoadClazz;
    }

    public Field getEntityIdField(Class<?> cls) {
        AccessibleFieldInfo fieldInfo = ldClass.get(cls);
        if (fieldInfo != null)
            return fieldInfo.field;
        return null;
    }

    public Method getEntityIdValGetter(Object obj) {
        if (obj == null)
            return null;
        Class<?> cls = obj.getClass();
        AccessibleFieldInfo fieldInfo = ldClass.get(cls);
        if (fieldInfo != null)
            return fieldInfo.idGetter;
        return null;
    }

    public DataEntry getDataEntryByName(String entryName) {
        init();
		DataEntry de = dataEntryMap.get(entryName);
		if(de==null){
			if(!beanCtx.containsBean(entryName))
				return null;
			DataEntryDefinition ddf= (DataEntryDefinition) beanCtx.getBean(entryName);
			ddf.init();
			List<Class> cls=new ArrayList<>();
			ddf.getClassNames().stream().forEach(cl->{
				try {
					Class c=Class.forName(cl);
					cls.add(c);
				} catch (ClassNotFoundException e) {
					logger.error(e,e);
				}
			});
			addDataEntry(ddf,cls);
		}
		return dataEntryMap.get(entryName);
    }

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.beanCtx=applicationContext;
	}

	private class AccessibleFieldInfo {
        public final Field field;
        public final Method idGetter;

        public AccessibleFieldInfo(Field field, Method idGetter) {
            this.field = field;
            this.idGetter = idGetter;
        }
    }
}
