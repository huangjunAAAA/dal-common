package com.boring.dal.cache.impl;

import com.boring.dal.cache.CacheHelper;
import com.boring.dal.cache.RealmCache;
import com.boring.dal.cache.SimpleCache;
import com.boring.dal.config.Constants;
import com.boring.dal.config.DataAccessConfig;
import com.boring.dal.config.DataEntry;
import com.boring.dal.dao.TxFlusher;
import com.boring.dal.json.GsonUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

@Component
@ConditionalOnMissingBean(AppCache.class)
public class ProcessCache implements RealmCache {

    private static final Logger logger = LogManager.getLogger("DAO");
    private static final ConcurrentHashMap<SimpleCache.CacheKey, Long> processDirty = new ConcurrentHashMap<>();
    @Resource
    protected DataAccessConfig dataAccessConfig;

    @Resource
    private SimpleCache xMemCache;

    @Resource
    private SimpleCache redisCache;

    @Autowired
    private TxFlusher txFlusher;
    
    @Resource
    private CacheHelper cacheHelper;

    @Override
    public void invalidateListData(DataEntry de, String key) {
        checkKeySanity(key);
        txFlusher.markDirty(de.getName(), key);
        xMemCache.deleteKey(de.getName(), key);
    }

    @Override
    public void invalidateEntity(Object id, Object entity) {
        if (logger.isDebugEnabled())
            logger.debug("invalidate entity:" + GsonUtil.toJson(entity));
        txFlusher.markDirty(entity.getClass().getTypeName(), id.toString());
        redisCache.deleteKey(entity.getClass().getTypeName(), id.toString());
        // update related list/map
        List<DataEntry> delst = dataAccessConfig.getClassRelatedListInfo(entity.getClass());
        if (logger.isDebugEnabled())
            logger.debug("invalidate entity:" + entity.getClass().getTypeName() + "|" + id + ", related list:" + delst.size());
        for (Iterator<DataEntry> iterator = delst.iterator(); iterator.hasNext(); ) {
            DataEntry de = iterator.next();
            String key = cacheHelper.getDataEntryCacheKeyWithEntities(de, entity);

            invalidateListData(de, key);
            xMemCache.deleteKey(de.getName(), key);
        }
    }


    @Override
    public int inspectList(DataEntry de, String key) {
        checkKeySanity(key);
        SimpleCache.CacheKey ckey = new SimpleCache.CacheKey(de.getName(), key);
        return inspectCache(ckey);
    }

    @Override
    public int inspectEntity(Object id, Class clazz) {
        SimpleCache.CacheKey ckey = new SimpleCache.CacheKey(clazz.getTypeName(), id.toString());
        return inspectCache(ckey);
    }

    private int inspectCache(SimpleCache.CacheKey ckey) {
        checkKeySanity(ckey.key);
        if (logger.isDebugEnabled())
            logger.debug("inspectCache:" + ckey.region + "," + ckey.key);
        Long ts = processDirty.get(ckey);
        if (ts != null) {
            long diff = System.currentTimeMillis() - ts;
            if (logger.isDebugEnabled())
                logger.debug("inspectCache:" + ckey.region + "," + ckey.key + ", dirty in process:" + ts + ", diff:" + diff);
            if (diff > dataAccessConfig.getCacheDirtyLast() * 2) {
                processDirty.remove(ckey);
                return Constants.CACHE_CLEAN;
            }
            if (diff < dataAccessConfig.getSlaveDirtyLast())
                return Constants.SLAVE_DIRTY;
            return Constants.CACHE_DIRTY;
        } else {
            ts = xMemCache.get(Constants.CACHE_UPDATING_PREFIX + ckey.region, ckey.key, Long.class);
            if (logger.isDebugEnabled())
                logger.debug("inspectCache:" + ckey.region + "," + ckey.key + ", val in memcached:" + ts + (ts != null ? ", diff:" + (System.currentTimeMillis() - ts) : ""));
            if (ts == null)
                return Constants.CACHE_CLEAN;
            if (ts == -1L)
                return Constants.CACHE_DIRTY;
            long diff = System.currentTimeMillis() - ts;
            if (diff > dataAccessConfig.getCacheDirtyLast()) {
                return Constants.CACHE_CLEAN;
            }
            if (diff < dataAccessConfig.getSlaveDirtyLast())
                return Constants.SLAVE_DIRTY;
            return Constants.CACHE_DIRTY;
        }
    }

    @Override
    public void setListData(String listName, String key, Object data) {
        checkKeySanity(key);
        xMemCache.setRaw(listName, key, data, 0);
    }

    @Override
    public <T> T getListData(String listName, String key, Supplier<T> s) {
        checkKeySanity(key);
        return xMemCache.get(listName, key, s);
    }

    @Override
    public <T> T getEntity(String id, Class<T> tClass) {
        Object mxid = redisCache.get(tClass.getTypeName(), Constants.MAXID_KEY, Object.class);
        if (logger.isDebugEnabled()) logger.debug(tClass.getTypeName() + " max id:" + mxid);
        if (mxid != null && mxid instanceof Long) {
            Long mxidl = (Long) mxid;
            Long idl = Long.parseLong(id);
            if (idl > mxidl)
                return (T) SimpleCache.NullObject;
        }
        return redisCache.getEntity(id.toString(), tClass);
    }

    @Override
    public void updateEntity(String id, Object entity, Object oldEntity) {
        if (logger.isDebugEnabled())
            logger.debug("updateEntity " + entity.getClass().getTypeName() + ":" + id + "\n" + GsonUtil.toJson(entity) + (oldEntity != null ? ("\noriginal:" + GsonUtil.toJson(oldEntity)) : ""));
        // update entity cache
        redisCache.setEntity(id.toString(), entity);
        if (oldEntity == null)
            return;

        // update related list/map
        List<DataEntry> delst = dataAccessConfig.getClassRelatedListInfo(entity.getClass());
        for (Iterator<DataEntry> iterator = delst.iterator(); iterator.hasNext(); ) {
            DataEntry de = iterator.next();

            Object[] newOrderBy = cacheHelper.getDataEntryOrderByVal(de, entity);
            Object[] oldOrderBy = cacheHelper.getDataEntryOrderByVal(de, oldEntity);


            String newkey = cacheHelper.getDataEntryCacheKeyWithEntities(de, entity);
            String oldkey = cacheHelper.getDataEntryCacheKeyWithEntities(de, oldEntity);

            if (!newkey.equals(oldkey) || !Arrays.equals(newOrderBy, oldOrderBy)) {
                if (logger.isDebugEnabled())
                    logger.debug("updateEntity " + entity.getClass().getTypeName() + ":" + id + ", listname:" + de.getName() + ", key:" + newkey + "|" + oldkey);
                invalidateListData(de, newkey);
                invalidateListData(de, oldkey);

                String newCkey = cacheHelper.getDataEntryCacheCountKey(newkey);
                String oldCkey = cacheHelper.getDataEntryCacheCountKey(oldkey);

                invalidateListData(de, newCkey);
                invalidateListData(de, oldCkey);
            }
        }
    }

    @Override
    public void updateEntityIfNotPresent(String id, Object entity) {
        redisCache.setIfNotPresent(entity.getClass().getTypeName(),id,entity,0);
    }

    @Override
    public void saveNewEntity(String id, Object entity) {
        if (logger.isDebugEnabled())
            logger.debug("saveNewEntity " + entity.getClass().getTypeName() + ":" + id + "\n" + GsonUtil.toJson(entity));
        // update entity cache
        redisCache.setEntity(id.toString(), entity);

        // update related list/map
        List<DataEntry> delst = dataAccessConfig.getClassRelatedListInfo(entity.getClass());
        for (Iterator<DataEntry> iterator = delst.iterator(); iterator.hasNext(); ) {
            DataEntry de = iterator.next();
            String key = cacheHelper.getDataEntryCacheKeyWithEntities(de, entity);
            invalidateListData(de, key);
            if (logger.isDebugEnabled())
                logger.debug("saveNewEntity " + entity.getClass().getTypeName() + ":" + id + ", listname:" + de.getName() + ", key:" + key);
        }

        // update max id
        xMemCache.setRaw(entity.getClass().getTypeName(), Constants.MAXID_KEY, id, 0);
    }

    @Override
    public <T> List<T> batchGetEntity(Class<T> clazz, List idList) {
        return redisCache.batchGetEntity(clazz, idList);
    }

    @Component
    public static class ThreadCache implements TxFlusher {

        private static final ThreadLocal<List<SimpleCache.CacheKey>> threadDirty = new ThreadLocal<>();

        private static final ScheduledExecutorService es = new ScheduledThreadPoolExecutor(Runtime.getRuntime().availableProcessors());

        @Autowired
        private SimpleCache simpleCache;

        @Resource
        private DataAccessConfig dataAccessConfig;

        public void markDirty(String region, String key) {
            checkKeySanity(key);
            if (logger.isDebugEnabled())
                logger.debug("markDirty: " + region + ", key:" + key);
            SimpleCache.CacheKey ckey = new SimpleCache.CacheKey(region, key);
            List<SimpleCache.CacheKey> lst = threadDirty.get();
            if (lst == null) {
                lst = new ArrayList<>();
                threadDirty.set(lst);
            }
            lst.add(ckey);
            simpleCache.setRaw(Constants.CACHE_UPDATING_PREFIX + region, key, System.currentTimeMillis() , 1);
        }

        @Override
        public void txClean(Object transaction) {
            if (logger.isDebugEnabled())
                logger.debug("txClean: " + transaction);
            final List<SimpleCache.CacheKey> lst = threadDirty.get();
            if (lst == null) {
                return;
            }
            threadDirty.remove();
            for (Iterator<SimpleCache.CacheKey> iterator = lst.iterator(); iterator.hasNext(); ) {
                SimpleCache.CacheKey key = iterator.next();
                simpleCache.setRaw(Constants.CACHE_UPDATING_PREFIX + key.region, key.key, System.currentTimeMillis() , 1);
                if (logger.isDebugEnabled())
                    logger.debug("txClean, mark key dirty:" + key.region + ", " + key.key);
            }
            es.schedule(() -> {
                for (Iterator<SimpleCache.CacheKey> iterator = lst.iterator(); iterator.hasNext(); ) {
                    SimpleCache.CacheKey key = iterator.next();
                    simpleCache.deleteKey(key.region, key.key);
                    processDirty.remove(key);
                    if (logger.isDebugEnabled())
                        logger.debug("txClean, cleansing:" + key.region + ", " + key.key);
                }
            }, dataAccessConfig.getCacheDirtyLast(), TimeUnit.MILLISECONDS);
        }
    }

    private static void checkKeySanity(String key){
        if(key.contains(Constants.KEY_PLACEHOLDER))
            throw new RuntimeException("key is ambiguous:"+key);
    }
}
