package com.boring.dal.cache.impl;

import com.boring.dal.cache.SimpleCache;
import com.boring.dal.config.Constants;
import com.boring.dal.config.DataEntry;
import com.boring.dal.json.GsonUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

@Component
public class AppCache extends ProcessCache {

    private static final Logger logger = LogManager.getLogger("DAO");

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private SimpleCache simpleCache;

    @Override
    public void invalidateListData(DataEntry de, String key) {
        if (logger.isDebugEnabled())
            logger.debug("invalidating list:" + de.getName() + ", mode:" + de.getCache() + ", key:" + key);
        ArrayList<String> keys = new ArrayList<>();

        if (Constants.CACHE_MODE_REDIS.equals(de.getCache())) {
            Cursor<String> sets = redisTemplate.opsForSet().scan(de.getName(), ScanOptions.scanOptions().match(key).count(100).build());
            sets.forEachRemaining((s)->keys.add(s));
            if(logger.isDebugEnabled()){
                Set<String> mbs = redisTemplate.opsForSet().members(de.getName());
                logger.debug("redis set:" + de.getName() + ", key:"+key+", members:"+ GsonUtil.toJson(mbs));
            }
            if(keys.size()>0) {
                redisTemplate.opsForSet().remove(de.getName(), keys.toArray(new String[0]));
                keys.stream().forEach((k)->simpleCache.deleteKey(de.getName(),k));
                if (logger.isDebugEnabled())
                    logger.debug("find related keys:" + (keys.size()) + ", list:" + de.getName() + ", key:" + key);
            }
        }else{
            keys.add(key);
        }
        for (Iterator<String> iterator = keys.iterator(); iterator.hasNext(); ) {
            String k = iterator.next();
            super.invalidateListData(de, k);
        }
    }

    @Override
    public void setListData(String listName, String key, Object data) {
        super.setListData(listName, key, data);
        DataEntry de = dataAccessConfig.getDataEntryByName(listName);
        if (logger.isDebugEnabled())
            logger.debug("setListData:" + de.getName() + ", mode:" + de.getCache() + ", key:" + key);
        if (Constants.CACHE_MODE_REDIS.equals(de.getCache()))
            redisTemplate.opsForSet().add(listName, key);
    }

}
