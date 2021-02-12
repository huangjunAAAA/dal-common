package com.boring.dal.cache.impl.cache;

import com.boring.dal.cache.ListCache;
import com.boring.dal.cache.RegionListCache;
import com.boring.dal.config.Constants;
import com.boring.dal.config.DataEntry;
import com.boring.dal.json.GsonUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;


public class RedisCollectionListCache implements RegionListCache {

    private static final Logger logger = LogManager.getLogger("DAO");

    private DataEntry de;

    @Resource
    private ListCacheFactory listCacheFactory;

    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    public RedisCollectionListCache(DataEntry de) {
        this.de = de;
    }

    @Override
    public List<String> matchKey(String pattern) {
        ArrayList<String> keys = new ArrayList<>();
        if(pattern.indexOf(Constants.KEY_PLACEHOLDER)==-1){
            keys.add(pattern);
            return keys;
        }

        Cursor<String> sets = redisTemplate.opsForSet().scan(de.getName(), ScanOptions.scanOptions().match(pattern).count(100).build());
        sets.forEachRemaining((s)->keys.add(s));

        if(logger.isDebugEnabled()){
            Set<String> mbs = redisTemplate.opsForSet().members(de.getName());
            logger.debug("redis set:" + de + ", key:"+pattern+", members:"+ GsonUtil.toJson(mbs));
        }
        return keys;
    }

    @Override
    public void addListKey(String key) {
        redisTemplate.opsForSet().add(de.getName(), key);
    }

    @Override
    public void removeListKey(List<String> keys) {
        redisTemplate.opsForSet().remove(de.getName(), keys.toArray(new String[0]));
    }

    @Override
    public void invalidateKey(String pattern) {
        if (logger.isDebugEnabled())
            logger.debug("invalidating list:" + de.getName() + ", mode:" + de.getCache() + ", key:" + pattern);
        ArrayList<String> keys = new ArrayList<>();

        if (de.getRelatedClass().size()>1) {
            RegionListCache regioncache = listCacheFactory.createRegionListCache(de);
            List<String> usedkeys = regioncache.matchKey(pattern);
            if(usedkeys.size()>0){
                regioncache.removeListKey(usedkeys);
                keys.stream().forEach((k)->{
                    keys.add(k);
                });
            }
        }else{
            keys.add(pattern);
        }
        for (Iterator<String> iterator = keys.iterator(); iterator.hasNext(); ) {
            String k = iterator.next();
            invalidateSingleListData(k);
        }
    }


    private void invalidateSingleListData(String key) {
        ListCache<Object> listcache = listCacheFactory.createListCache(de,key, false);
        listcache.markDirty();
        listcache.delete();
    }
}
