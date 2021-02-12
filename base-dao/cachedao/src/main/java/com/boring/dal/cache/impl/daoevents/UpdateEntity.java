package com.boring.dal.cache.impl.daoevents;

import com.boring.dal.cache.CacheHelper;
import com.boring.dal.cache.EntityCache;
import com.boring.dal.cache.ListCache;
import com.boring.dal.cache.RegionListCache;
import com.boring.dal.cache.impl.cache.EntityCacheFactory;
import com.boring.dal.cache.impl.cache.ListCacheFactory;
import com.boring.dal.cache.impl.daoevents.handling.EventContext;
import com.boring.dal.cache.impl.daoevents.handling.EventHandler;
import com.boring.dal.config.Constants;
import com.boring.dal.config.DataAccessConfig;
import com.boring.dal.config.DataEntry;
import com.boring.dal.dao.TxFlusher;
import com.boring.dal.json.GsonUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;

import static com.boring.dal.cache.impl.daoevents.handling.EventHandler.EVENT_ENTITYUPDATED;

@Component(EVENT_ENTITYUPDATED)
public class UpdateEntity implements EventHandler {
    private static final Logger logger = LogManager.getLogger("DAO");
    @Resource
    private CacheHelper cacheHelper;
    @Resource
    private EntityCacheFactory entityCacheFactory;
    @Resource
    private ListCacheFactory listCacheFactory;
    @Resource
    protected DataAccessConfig dataAccessConfig;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    @Override
    public void handleEvent(EventContext env) {
        Object entity = env.newObj;
        Object oldEntity = env.oldObj;
        Object id = env.id;
        if (logger.isDebugEnabled())
            logger.debug("updateEntity " + entity.getClass().getTypeName() + ":" + id + "\n" + GsonUtil.toJson(entity) + (oldEntity != null ? ("\noriginal:" + GsonUtil.toJson(oldEntity)) : ""));
        // update entity cache
        EntityCache<Object> cache = entityCacheFactory.createEntityCache(entity.getClass().getTypeName(), id.toString(), false);
        cache.update(entity);
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

                String nCkey = cacheHelper.getDataEntryCacheCountKey(newkey);
                String oCkey = cacheHelper.getDataEntryCacheCountKey(oldkey);

                invalidateListData(de, nCkey);
                invalidateListData(de, oCkey);
            }
        }
    }

    private void invalidateListData(DataEntry de, String key) {
        if (logger.isDebugEnabled())
            logger.debug("invalidating list:" + de.getName() + ", mode:" + de.getCache() + ", key:" + key);
        if (de.getRelatedClass().size()>1) {
            RegionListCache regioncache = listCacheFactory.createRegionListCache(de);
            regioncache.invalidateKey(key);
        }else{
            ListCache<Object> listcache = listCacheFactory.createListCache(de, key, false);
            listcache.markDirty();
            listcache.delete();
        }
    }
}
