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
import com.boring.dal.json.GsonUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.boring.dal.cache.impl.daoevents.handling.EventHandler.EVENT_NEWENTITYSAVED;

@Component(EVENT_NEWENTITYSAVED)
public class SaveNewEntity implements EventHandler {
    private static final Logger logger = LogManager.getLogger("DAO");
    @Resource
    private CacheHelper cacheHelper;
    @Resource
    private EntityCacheFactory entityCacheFactory;
    @Resource
    private ListCacheFactory listCacheFactory;
    @Resource
    protected DataAccessConfig dataAccessConfig;
    @Override
    public void handleEvent(EventContext env) {
        Object entity = env.newObj;
        Object id = env.id;
        if (logger.isDebugEnabled())
            logger.debug("saveNewEntity " + entity.getClass().getTypeName() + ":" + id + "\n" + GsonUtil.toJson(entity));

        // update entity cache
        EntityCache<Object> cache = entityCacheFactory.createEntityCache(entity.getClass().getTypeName(), id.toString(), false);
        cache.update(entity);

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
        cache.setMaxID(id);
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
