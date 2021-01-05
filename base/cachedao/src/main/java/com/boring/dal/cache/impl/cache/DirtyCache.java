package com.boring.dal.cache.impl.cache;

import com.boring.dal.cache.CacheHelper;
import com.boring.dal.cache.construct.ObjectCacheKey;
import com.boring.dal.config.Constants;
import com.boring.dal.config.DataAccessConfig;
import com.boring.dal.dao.TxFlusher;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.persistence.Column;

@Component
public class DirtyCache {

    public static final Logger logger = LogManager.getLogger("DAO");

    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    @Autowired
    private CacheHelper cacheHelper;

    @Resource
    protected DataAccessConfig dataAccessConfig;

    public int inspect(String region,String key) {
        ObjectCacheKey ckey = new ObjectCacheKey(region, key);
        cacheHelper.checkKeySanity(key);
        if (logger.isDebugEnabled())
            logger.debug("inspectCache:" + region + "," + key);
        Long ts = ProcessCtxHolder.processDirty.get(ckey);
        if (ts != null) {
            long diff = System.currentTimeMillis() - ts;
            if (logger.isDebugEnabled())
                logger.debug("inspectCache:" + ckey.region + "," + ckey.key + ", dirty in process:" + ts + ", diff:" + diff);
            if (diff > dataAccessConfig.getCacheDirtyLast() * 2) {
                ProcessCtxHolder.processDirty.remove(ckey);
                return Constants.CACHE_CLEAN;
            }
            if (diff < dataAccessConfig.getSlaveDirtyLast())
                return Constants.SLAVE_DIRTY;
            return Constants.CACHE_DIRTY;
        } else {
            String rkey=cacheHelper.getRegionKey(Constants.CACHE_UPDATING_PREFIX + region,key);
            ts = (Long) redisTemplate.opsForValue().get(rkey);
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
}
