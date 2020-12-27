package com.boring.dal.cache.impl.cache;

import com.boring.dal.cache.BatchEntityCache;
import com.boring.dal.cache.EntityCache;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class EntityCacheFactory {
    public <T> EntityCache<T> createEntityCache(String region,String key, boolean fill){
        return null;
    }


    public <T> BatchEntityCache<T> createBatchCache(String region, List<String> keys, boolean fill){
        return null;
    }
}
