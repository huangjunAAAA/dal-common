package com.boring.dal.cache.impl.cache;

import com.boring.dal.cache.ListCache;
import com.boring.dal.config.DataEntry;
import org.springframework.stereotype.Component;

@Component
public class ListCacheFactory {
    public <T> ListCache<T> createListCache(DataEntry de, String key, boolean fill){
        return null;
    }

}
