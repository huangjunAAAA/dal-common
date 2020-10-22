package com.boring.dal.config;

import com.boring.dal.YamlFileLoader;
import com.boring.dal.YamlPropertySourceFactory;
import com.boring.dal.config.sqllinker.DruidSQLLinker;
import com.boring.dal.config.sqllinker.FieldSQLConnector;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.reflections8.Reflections;
import org.reflections8.scanners.SubTypesScanner;
import org.reflections8.scanners.TypeAnnotationsScanner;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

@Configuration
@PropertySource(value = {"application.yml", "bootstrap.yml"}, factory = YamlPropertySourceFactory.class)
@Component("dummy-dac")
public class DataAccessConfig {

    private static final Logger logger = LogManager.getLogger("DAO");

    private volatile DataAccessConfigFile raw;

    private volatile Map<Class, List<DataEntry>> associatedDataEntries;

    private Map<String, DataEntry> dataEntryMap;

    private FieldSQLConnector linker = new DruidSQLLinker();

    private volatile LinkedHashMap<Class, AccessibleFieldInfo> ldClass;
    @Value("${dao.config}")
    private String configFile;

    private static Field getIdField(Class<?> cls) {
        Field[] fields = cls.getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            Field f = fields[i];
            if (f.getAnnotation(Id.class) != null)
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

    @Bean(name = "dataAccessConfig")
    public DataAccessConfig loadConfig() {
        DataAccessConfigFile dFile = YamlFileLoader.loadConfigFromPath(configFile, DataAccessConfigFile.class);
        if (dFile != null) {
            DataAccessConfig dataAccessConfig = new DataAccessConfig();
            dataAccessConfig.raw = dFile;
            dataAccessConfig.init();
            return dataAccessConfig;
        }
        return null;
    }

    public void init() {
        if (raw == null)
            return;
        if (associatedDataEntries == null) {
            synchronized (this) {
                if (associatedDataEntries == null) {
                    associatedDataEntries = new HashMap<>();
                    dataEntryMap = new HashMap<>();
                    for (Iterator<DataEntry> iterator = raw.getData().iterator(); iterator.hasNext(); ) {
                        DataEntry de = iterator.next();
                        parseDataEntry(de);
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
                            if (de.getRelatedClass().size() == 1) {
                                de.setCache(Constants.CACHE_MODE_MEM);
                            } else {
                                de.setCache(Constants.CACHE_MODE_REDIS);
                            }
                        }
                    }
                }
            }
        }

    }

    public List<DataEntry> getClassRelatedListInfo(Class cls) {
        init();
        return associatedDataEntries.get(cls);
    }

    private void parseDataEntry(DataEntry entry) {
        loadAllClazz();
        linker.linkDataEntry(entry, new ArrayList<>(ldClass.keySet()));
        logger.info("Complete parsing data entry:" + entry.getName() + ", related class:" + entry.getRelatedClass());
    }

    public List<Class> loadAllClazz() {
        if (raw == null)
            return null;
        if (ldClass == null)
            synchronized (this) {
                if (ldClass == null) {
                    ArrayList<Class> toLoadClazz = new ArrayList<>();
                    ScannedModel dm = raw.getObjects();
                    Reflections reflections = new Reflections(dm.getScanPkg(), new TypeAnnotationsScanner(), new SubTypesScanner());

                    Set<Class<?>> tmp = reflections.getTypesAnnotatedWith(Entity.class);
                    toLoadClazz.addAll(tmp);

                    if (dm.getDefined() != null) {
                        for (Iterator<String> iterator = dm.getDefined().iterator(); iterator.hasNext(); ) {
                            String clsname = iterator.next();
                            try {
                                Class<?> cls = Class.forName(clsname);
                                if (cls.getAnnotation(Entity.class) != null) {
                                    if (toLoadClazz.indexOf(cls) == -1) {
                                        toLoadClazz.add(cls);
                                    }
                                }
                            } catch (ClassNotFoundException e) {
                                logger.error(e, e);
                            }
                        }
                    }

                    ldClass = new LinkedHashMap<>();
                    for (Iterator<Class> iterator = toLoadClazz.iterator(); iterator.hasNext(); ) {
                        Class cls = iterator.next();
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
            }
        return new ArrayList<>(ldClass.keySet());
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
        return dataEntryMap.get(entryName);
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
