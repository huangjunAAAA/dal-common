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

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadCache implements TxFlusher {

    public static final Logger logger = LogManager.getLogger("DAO");

    private static final ThreadLocal<List<ObjectCacheKey>> threadDirty = new ThreadLocal<>();

    private static final ScheduledExecutorService es = new ScheduledThreadPoolExecutor(Runtime.getRuntime().availableProcessors());

    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    @Autowired
    private CacheHelper cacheHelper;

    @Resource
    private DataAccessConfig dataAccessConfig;

    public void markDirty(String region, String key) {
        cacheHelper.checkKeySanity(key);
        if (logger.isDebugEnabled())
            logger.debug("markDirty: " + region + ", key:" + key);
        ObjectCacheKey ckey = new ObjectCacheKey(region, key);
        List<ObjectCacheKey> lst = threadDirty.get();
        if (lst == null) {
            lst = new ArrayList<>();
            threadDirty.set(lst);
        }
        lst.add(ckey);
        String rkey=cacheHelper.getRegionKey(Constants.CACHE_UPDATING_PREFIX + region,key);
        redisTemplate.opsForValue().set(rkey,System.currentTimeMillis());
    }

    @Override
    public void txClean(Object transaction) {
        if (logger.isDebugEnabled())
            logger.debug("txClean: " + transaction);
        final List<ObjectCacheKey> lst = threadDirty.get();
        if (lst == null) {
            return;
        }
        threadDirty.remove();
        for (Iterator<ObjectCacheKey> iterator = lst.iterator(); iterator.hasNext(); ) {
            ObjectCacheKey key = iterator.next();
            String rkey=cacheHelper.getRegionKey(Constants.CACHE_UPDATING_PREFIX + key.region,key.key);
            redisTemplate.opsForValue().set(rkey,System.currentTimeMillis());
            if (logger.isDebugEnabled())
                logger.debug("txClean, mark key dirty:" + key.region + ", " + key.key);
        }
        es.schedule(() -> {
            for (Iterator<ObjectCacheKey> iterator = lst.iterator(); iterator.hasNext(); ) {
                ObjectCacheKey key = iterator.next();
                String rkey=cacheHelper.getRegionKey(Constants.CACHE_UPDATING_PREFIX + key.region,key.key);
                redisTemplate.delete(rkey);
                ProcessCtxHolder.processDirty.remove(key);
                if (logger.isDebugEnabled())
                    logger.debug("txClean, cleansing:" + key.region + ", " + key.key);
            }
        }, dataAccessConfig.getCacheDirtyLast(), TimeUnit.MILLISECONDS);
    }
}
