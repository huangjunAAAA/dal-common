package com.boring.dal.cache.impl;

import com.boring.dal.cache.CacheHelper;
import com.boring.dal.cache.ComprehensiveDao;
import com.boring.dal.cache.RealmCache;
import com.boring.dal.cache.SimpleCache;
import com.boring.dal.config.Constants;
import com.boring.dal.config.DalCached;
import com.boring.dal.config.DataAccessConfig;
import com.boring.dal.config.DataEntry;
import com.boring.dal.dao.CollectionDao;
import com.boring.dal.dao.EntityDao;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

@Component
public class DaoFacade implements ComprehensiveDao {

    private static final Logger logger = LogManager.getLogger("DAO");

    @Autowired
    private EntityDao entityDao;

    @Autowired
    private CollectionDao collectionDao;

    @Autowired
    private RealmCache realmCache;

    @Resource
    private DataAccessConfig dataAccessConfig;

    @Autowired
    private CacheHelper cacheHelper;

    private static List subList(List list, int start, int toIndex) {
        if (start == 0 && toIndex == list.size() - 1)
            return list;
        return new ArrayList(list.subList(start, toIndex));
    }

    @Override
    public List<Object[]> getDataListMulti(String listName, Object[] params, Integer start, Integer count) throws Exception {
        DataEntry de = dataAccessConfig.getDataEntryByName(listName);
        if (de == null)
            throw new Exception("Data Entry Not Found:" + listName);

        if(de.getValueProperties().size()==1){
            if (Constants.CACHE_MODE_NONE.equals(de.getCache())) {
                logger.warn("no cache configured:" + listName);
                return collectionDao.getDataListSingle(listName,params,start,count,0,false);
            }
            List tmp= getCachedDataList(listName, params, start, count,
                    (fetchOp) -> collectionDao.getDataListSingle(fetchOp.listName, fetchOp.params, fetchOp.start, fetchOp.count,0, fetchOp.masterAccess));
            List<Object[]> ret=new ArrayList<>();
            tmp.forEach((e)->ret.add(new Object[]{e}));
            return ret;
        }else {
            if (Constants.CACHE_MODE_NONE.equals(de.getCache())) {
                logger.warn("no cache configured:" + listName);
                return collectionDao.getDataListMulti(listName,params,start,count,false);
            }
            return getCachedDataList(listName, params, start, count,
                    (fetchOp) -> collectionDao.getDataListMulti(fetchOp.listName, fetchOp.params, fetchOp.start, fetchOp.count, fetchOp.masterAccess));
        }
    }

    @Override
    public <T> List<T> getDataListSingle(String listName, Object[] params, Integer start, Integer count, Integer colIdx) throws Exception {
        DataEntry de = dataAccessConfig.getDataEntryByName(listName);
        if (de == null)
            throw new Exception("Data Entry Not Found:" + listName);
        if (Constants.CACHE_MODE_NONE.equals(de.getCache())) {
            logger.warn("no cache configured:" + listName);
            return collectionDao.getDataListSingle(listName,params,start,count,colIdx,false);
        }
        if(de.getValueProperties().size()==1){
            return getCachedDataList(listName, params, start, count,
                    (fetchOp) -> collectionDao.getDataListSingle(fetchOp.listName, fetchOp.params, fetchOp.start, fetchOp.count,0, fetchOp.masterAccess));
        }else {
            List<Object[]> cols = getCachedDataList(listName, params, start, count,
                    (fetchOp) -> collectionDao.getDataListMulti(fetchOp.listName, fetchOp.params, fetchOp.start, fetchOp.count, fetchOp.masterAccess));
            List ret = new ArrayList<>();
            cols.forEach((c) -> ret.add(c[colIdx]));
            return ret;
        }
    }

    private <T> List<T> getCachedDataList(String listName, Object[] params, Integer start, Integer count, DBDataFetcher<FetchOp<T>, List<T>> dbget) throws Exception {
        DataEntry de = dataAccessConfig.getDataEntryByName(listName);
        if (de == null)
            throw new Exception("Data Entry Not Found:" + listName);
        if (logger.isDebugEnabled())
            logger.debug("getCachedDataList:" + listName + ", params:" + Arrays.toString(params) + ", start:" + start + ", count:" + count );

        String key = cacheHelper.getDataEntryCacheKeyWithValue(listName, params);
        int cachestatus = realmCache.inspectList(de, key);
        if (logger.isDebugEnabled())
            logger.debug("getCachedDataList:" + listName + ", params:" + Arrays.toString(params) + ", cache status:" + cachestatus);
        if (cachestatus != Constants.CACHE_CLEAN) {
            return dbget.apply(new FetchOp<T>(listName, params, start, count,  cachestatus == Constants.SLAVE_DIRTY));
        }
        List<T> cached = realmCache.getListData(listName, key, ArrayList<T>::new);
        int toIndex = start + count;
        if (cached != null && cached.size() > 0) {
            if (cached.size() >= start + count) {
                if (logger.isDebugEnabled())
                    logger.debug("getCachedDataList:" + listName + ", params:" + Arrays.toString(params) + ", cache hit.");
                return subList(cached, start, toIndex);
            }
            int actualCount = countDataList(listName, params);
            if (start >= actualCount)
                return new ArrayList<>();
            if (toIndex > actualCount)
                toIndex = actualCount;
            List<T> additional = dbget.apply(new FetchOp(listName, params, cached.size(), toIndex - cached.size(),  cachestatus == Constants.SLAVE_DIRTY));
            cached.addAll(additional);
            realmCache.setListData(listName, key, cached);
            if (logger.isDebugEnabled())
                logger.debug("getCachedDataList:" + listName + ", params:" + Arrays.toString(params) + ", cache partial.");
            return subList(cached, start, toIndex);
        } else {
            List<T> full = dbget.apply(new FetchOp(listName, params, 0, toIndex,  cachestatus == Constants.SLAVE_DIRTY));
            realmCache.setListData(listName, key, full);
            if (logger.isDebugEnabled())
                logger.debug("getCachedDataList:" + listName + ", params:" + Arrays.toString(params) + ", cache empty.");
            int actualCount = countDataList(listName, params);
            if (start >= actualCount)
                return new ArrayList<>();
            if (toIndex > actualCount)
                toIndex = actualCount;
            return subList(full, start, toIndex);
        }
    }

    @Override
    public Integer countDataList(String listName, Object[] params) throws Exception {
        return getCachedSingleData(listName, params, null,
                fetchOp -> collectionDao.countDataList(fetchOp.listName, fetchOp.params, fetchOp.masterAccess),
                fetchOp -> cacheHelper.getDataEntryCacheCountKeyWithValue(fetchOp.listName, fetchOp.params));
    }

    private <T> T getCachedSingleData(String listName, Object[] params, Integer colIdx, DBDataFetcher<FetchOp<T>, T> getter, Function<FetchOp<T>, String> keyGen) throws Exception {
        DataEntry de = dataAccessConfig.getDataEntryByName(listName);
        if (de == null)
            throw new Exception("Data Entry Not Found:" + listName);

        if (logger.isDebugEnabled())
            logger.debug("getCachedSingleData:" + listName + ", params:" + Arrays.toString(params) + ", colIdx:" + colIdx);
        FetchOp<T> fetchOp = new FetchOp<T>(listName, params, null, null,  false);
        if (Constants.CACHE_MODE_NONE.equals(de.getCache())) {
            logger.warn("no cache configured:" + listName);
            return getter.apply(fetchOp);
        }
        String key = keyGen.apply(fetchOp);
        int cachestatus = realmCache.inspectList(de, key);
        if (logger.isDebugEnabled())
            logger.debug("getCachedSingleData:" + listName + ", params:" + Arrays.toString(params) + ", cache status:" + cachestatus);
        if (realmCache.inspectList(de, key) != Constants.CACHE_CLEAN) {
            return getter.apply(new FetchOp<>(listName, params, null, null, cachestatus == Constants.SLAVE_DIRTY));
        }
        T val = realmCache.getListData(listName, key, () -> null);
        if (val == null) {
            val = getter.apply(new FetchOp<>(listName, params, null, null, cachestatus == Constants.SLAVE_DIRTY));
            realmCache.setListData(listName, key, val);
            if (logger.isDebugEnabled())
                logger.debug("getCachedSingleData:" + listName + ", params:" + Arrays.toString(params) + ", cache empty.");
        } else {
            if (logger.isDebugEnabled())
                logger.debug("getCachedSingleData:" + listName + ", params:" + Arrays.toString(params) + ", cache hit.");
        }
        return val;
    }

    @Override
    public <T> T getDataMapSingle(String mapName, Object[] params, Integer colIdx) throws Exception {
        DataEntry de = dataAccessConfig.getDataEntryByName(mapName);
        if (de == null)
            throw new Exception("Data Entry Not Found:" + mapName);
        if (Constants.CACHE_MODE_NONE.equals(de.getCache())) {
            logger.warn("no cache configured:" + mapName);
            return collectionDao.getDataMapSingle(mapName,params,colIdx,false);
        }
        if(de.getValueProperties().size()==1){
            return getCachedSingleData(mapName, params, colIdx,
                    fetchOp -> collectionDao.getDataMapSingle(fetchOp.listName, fetchOp.params,0, fetchOp.masterAccess),
                    fetchOp -> cacheHelper.getDataEntryCacheKeyWithValue(fetchOp.listName, fetchOp.params));
        }else {
            Object[] cols = getCachedSingleData(mapName, params, colIdx,
                    fetchOp -> collectionDao.getDataMapMulti(fetchOp.listName, fetchOp.params, fetchOp.masterAccess),
                    fetchOp -> cacheHelper.getDataEntryCacheKeyWithValue(fetchOp.listName, fetchOp.params));
            return (T) cols[colIdx];
        }
    }

    @Override
    public Object[] getDataMapMulti(String mapName, Object[] params) throws Exception {
        DataEntry de = dataAccessConfig.getDataEntryByName(mapName);
        if (de == null)
            throw new Exception("Data Entry Not Found:" + mapName);
        if(de.getValueProperties().size()==1){
            if (Constants.CACHE_MODE_NONE.equals(de.getCache())) {
                logger.warn("no cache configured:" + mapName);
                return collectionDao.getDataMapSingle(mapName,params,0,false);
            }
            Object tmp = getCachedSingleData(mapName, params, null,
                    fetchOp -> collectionDao.getDataMapSingle(fetchOp.listName, fetchOp.params, 0, fetchOp.masterAccess),
                    fetchOp -> cacheHelper.getDataEntryCacheKeyWithValue(fetchOp.listName, fetchOp.params));
            return new Object[]{tmp};
        }else{
            if (Constants.CACHE_MODE_NONE.equals(de.getCache())) {
                logger.warn("no cache configured:" + mapName);
                return collectionDao.getDataMapMulti(mapName,params,false);
            }
            return getCachedSingleData(mapName, params, null,
                    fetchOp -> collectionDao.getDataMapMulti(fetchOp.listName, fetchOp.params, fetchOp.masterAccess),
                    fetchOp -> cacheHelper.getDataEntryCacheKeyWithValue(fetchOp.listName, fetchOp.params));
        }
    }

    @Override
    public <T> T get(Object id, Class<T> clazz) throws Exception {
        if (null == dataAccessConfig.getEntityIdField(clazz)) {
            throw new Exception("Object not loaded:" + clazz);
        }
        if (clazz.getAnnotation(DalCached.class) == null)
            return entityDao.get(id, clazz);

        T obj = realmCache.getEntity(id.toString(), clazz);
        if (obj == SimpleCache.NullObject) {
            if (logger.isDebugEnabled()) logger.debug("get:" + id + ", class:" + clazz.getTypeName() + ", cache null.");
            return null;
        }
        if (obj == null) {
            obj = entityDao.get(id, clazz);
            realmCache.updateEntityIfNotPresent(id.toString(), obj);
            if (logger.isDebugEnabled())
                logger.debug("get:" + id + ", class:" + clazz.getTypeName() + ", cache empty.");
        } else {
            if (logger.isDebugEnabled())
                logger.debug("get:" + id + ", class:" + clazz.getTypeName() + ", cache hit.");
        }
        return obj;
    }

    @Override
    public Object get(Object id, String clazz) throws Exception {
        Class t = Class.forName(clazz);
        return get(id, t);
    }

    public Object save(Object obj) throws Exception {
        if (obj == null)
            throw new Exception("trying to save Null.");
        if (null == dataAccessConfig.getEntityIdField(obj.getClass())) {
            throw new Exception("Object not loaded:" + obj.getClass());
        }
        Object id = entityDao.save(obj);
        realmCache.saveNewEntity(id.toString(), obj);
        return id;
    }

    public void update(Object obj) throws Exception {
        if (obj == null)
            throw new Exception("trying to update Null.");
        if (null == dataAccessConfig.getEntityIdField(obj.getClass())) {
            throw new Exception("Object not loaded:" + obj.getClass());
        }
        Object id = cacheHelper.getObjectIdVal(obj);
        Object oldObj = get(id, obj.getClass());
        entityDao.update(obj);
        realmCache.updateEntity(id.toString(), obj, oldObj);
    }

    public List batchSave(List objList) throws Exception {
        if (objList == null)
            throw new Exception("trying to batchSave Null.");
        List<Serializable> idList = entityDao.batchSave(objList);
        for (int i = 0; i < objList.size(); i++) {
            Object obj = objList.get(i);
            Serializable id = idList.get(i);
            realmCache.saveNewEntity(id.toString(), obj);
        }
        return idList;
    }

    @Override
    public void delete(Object id, String clazz) throws Exception {
        entityDao.delete(id, clazz);
        realmCache.invalidateEntity(id, clazz);
    }

    @Override
    public <T> List<T> batchGet(List idList, Class<T> clazz) throws Exception {
        if (clazz.getAnnotation(DalCached.class) == null)
            return entityDao.batchGet(idList, clazz);

        int nullnum = 0;
        List<T> ret = realmCache.batchGetEntity(clazz, idList);
        int cserved=0;
        ArrayList idremains = new ArrayList();
        for (int i = 0; i < ret.size(); i++) {
            T val = ret.get(i);
            if (val == null) {
                idremains.add(idList.get(i));
            }else{
                cserved++;
            }
        }

        List<T> rest = entityDao.batchGet(idremains, clazz);
        for (int i = 0; i < ret.size(); i++) {
            T val = ret.get(i);
            if (val == null) {
                val = rest.remove(0);
            }
            if (val != null) {
                realmCache.updateEntity(idList.get(i).toString(), val, null);
            } else
                nullnum++;
            ret.set(i, val);
        }
        if (logger.isDebugEnabled())
            logger.debug("batch get:" + idList.size() + ", class:" + clazz.getTypeName() + ", cache served:" + cserved + ", db served:" + (idremains.size() - nullnum) + ", Null:" + nullnum);
        return ret;
    }

    @Override
    public List batchGet(List idList, String clazz) throws Exception {
        Class t = Class.forName(clazz);
        return batchGet(idList, t);
    }

    @Override
    public <E> List<E> getDataListEntity(String listName, Object[] params, Integer start, Integer count, Class<E> clazz, Integer colIdx) throws Exception {
        List idlist = getDataListSingle(listName,params,start,count,colIdx);
        return batchGet(idlist, clazz);
    }

    @Override
    public List getDataListEntity(String listName, Object[] params, Integer start, Integer count, String clazz, Integer colIdx) throws Exception {
        Class t = Class.forName(clazz);
        return getDataListEntity(listName, params, start, count, t, colIdx);
    }
}
