package com.boring.dal.cache;

import com.boring.dal.config.DataAccessConfig;
import com.boring.dal.config.DataEntry;
import com.boring.dal.config.SQLVarInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

import static com.boring.dal.config.Constants.*;

@Component
public class CacheHelper {
    private static final Logger logger = LogManager.getLogger("DAO");

    @Resource
    private DataAccessConfig dataAccessConfig;

    public Object getObjectIdVal(Object obj) {
        Method getter = dataAccessConfig.getEntityIdValGetter(obj);
        if (getter != null) {
            try {
                Object idval = getter.invoke(obj);
                return idval;
            } catch (Exception e) {
                logger.error(e, e);
            }
        }
        return null;
    }

    public Object[] getDataEntryOrderByVal(DataEntry entry, Object... target) {
        LinkedList<Method> op = entry.getOrderByProperties();
        if (target == null || target.length == 0 || op == null || op.size() == 0)
            return null;
        HashMap<Class, Object> ocmap = new HashMap<>();
        for (int i = 0; i < target.length; i++) {
            Object t = target[i];
            ocmap.put(t.getClass(), t);
        }

        Object[] ret = new Object[op.size()];
        int i = 0;
        for (Iterator<Method> iterator = op.iterator(); iterator.hasNext(); ) {
            Method m = iterator.next();
            Object t = ocmap.get(m.getDeclaringClass());
            if(t!=null)
                try {
                    ret[i] = m.invoke(t);
                } catch (Exception e) {
                    logger.error(e, e);
                } finally {
                    i++;
                }
        }
        return ret;
    }

    public String getDataEntryCacheCountKey(String entryKey) {
        return entryKey + LIST_CNT_SUFFIX;
    }

    public String getDataEntryCacheCountKeyWithEntities(DataEntry entry, Object... target) {
        return getDataEntryCacheKeyWithEntities(entry, target) + LIST_CNT_SUFFIX;
    }

    public String getDataEntryCacheCountKeyWithEntities(String entryName, Object... target) {
        DataEntry de = dataAccessConfig.getDataEntryByName(entryName);
        if (de == null)
            return null;
        return getDataEntryCacheCountKeyWithEntities(de, target);
    }

    public String getDataEntryCacheCountKeyWithValue(DataEntry entry, Object... target) {
        return getDataEntryCacheKeyWithValue(entry, target) + LIST_CNT_SUFFIX;
    }

    public String getDataEntryCacheCountKeyWithValue(String entryName, Object... target) {
        DataEntry de = dataAccessConfig.getDataEntryByName(entryName);
        if (de == null)
            return null;
        return getDataEntryCacheCountKeyWithValue(de, target);
    }

    public String getDataEntryCacheKeyWithEntities(String entryName, Object... target) {
        DataEntry de = dataAccessConfig.getDataEntryByName(entryName);
        if (de == null)
            return null;
        return getDataEntryCacheKeyWithEntities(de, target);
    }

    public String getDataEntryCacheKeyWithValue(String entryName, Object... target){
        DataEntry de = dataAccessConfig.getDataEntryByName(entryName);
        if (de == null)
            return null;
        return getDataEntryCacheKeyWithValue(de,target);
    }

    public String getDataEntryCacheKeyWithValue(DataEntry entry, Object... target){
        if (target == null || target.length == 0)
            return "";
        StringBuilder keypattern = new StringBuilder();
        for (int i = 0; i < target.length; i++) {
            Object v = target[i];
            keypattern.append(KEY_DELIMITER).append(v);
        }
        keypattern.append(KEY_DELIMITER);
        String k = keypattern.toString();
        return k;
    }

    public String getDataEntryCacheKeyWithEntities(DataEntry entry, Object... target) {
        if (target == null || target.length == 0)
            return "";
        HashMap<Class, Object> ocmap = new HashMap<>();
        for (int i = 0; i < target.length; i++) {
            Object t = target[i];
            ocmap.put(t.getClass(), t);
        }

        StringBuilder keypattern = new StringBuilder();
        LinkedList<SQLVarInfo> km = entry.getKeyProperties();
        for (Iterator<SQLVarInfo> iterator = km.iterator(); iterator.hasNext(); ) {
            SQLVarInfo varInfo = iterator.next();
            if (varInfo == null || varInfo.getter == null) {
                keypattern.append(KEY_DELIMITER).append(KEY_PLACEHOLDER);
            } else {
                Class<?> dclazz = varInfo.getter.getDeclaringClass();
                Object t = ocmap.get(dclazz);
                if (t == null) {
                    keypattern.append(KEY_DELIMITER).append(KEY_PLACEHOLDER);
                } else try {
                    Object v = varInfo.getter.invoke(t);
                    keypattern.append(KEY_DELIMITER).append(v);
                } catch (Exception e) {
                    logger.error(e, e);
                }
            }
        }
        keypattern.append(KEY_DELIMITER);
        String k = keypattern.toString();
        return k;
    }
}
