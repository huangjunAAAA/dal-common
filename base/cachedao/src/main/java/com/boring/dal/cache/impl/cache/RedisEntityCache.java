package com.boring.dal.cache.impl.cache;

import com.boring.dal.cache.CacheHelper;
import com.boring.dal.cache.EntityCache;
import com.boring.dal.cache.construct.ObjectCacheKey;
import com.boring.dal.cache.sanitizer.KeySanitizer;
import com.boring.dal.config.Constants;
import com.boring.dal.config.DataAccessConfig;
import com.boring.dal.dao.TxFlusher;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.Resource;

public class RedisEntityCache implements EntityCache {

    public RedisEntityCache(String region, String key) {
        this.region = region;
        this.key = key;
    }

    public static final Logger logger = LogManager.getLogger("DAO");

    @Autowired(required = false)
    private TxFlusher txFlusher;

    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    @Autowired
    private CacheHelper cacheHelper;

    @Resource
    private DirtyCache dirtyCache;

    private String region;

    private String key;

    private Object val;

    @Override
    public int inspect() {
        return dirtyCache.inspect(region,key);
    }

    @Override
    public void markDirty() {
        txFlusher.markDirty(region,key);
    }

    @Override
    public void flush() {
        val=null;
    }

    @Override
    public Object get() {
        if(val!=null)
            return val;
        String rkey=cacheHelper.getRegionKey(region,key);
        val=redisTemplate.opsForValue().get(rkey);
        return val;
    }

    @Override
    public void update(Object val) {
        String rkey=cacheHelper.getRegionKey(region,key);
        redisTemplate.opsForValue().set(rkey,val);
    }

    @Override
    public boolean updateIfAbsent(Object val) {
        String rkey=cacheHelper.getRegionKey(region,key);
        return redisTemplate.opsForValue().setIfAbsent(rkey,val);
    }

    @Override
    public void delete() {
        String rkey=cacheHelper.getRegionKey(region,key);
        redisTemplate.delete(rkey);
    }

    @Override
    public void setMaxID(Object id) {
        String rkey = cacheHelper.getRegionKey(region + Constants.MAXID_KEY, id.toString());
        redisTemplate.opsForValue().set(rkey,id);
    }
}
