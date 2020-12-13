package com.boring.dal.cache.impl;

import com.boring.dal.cache.ListCache;
import com.boring.dal.config.DataEntry;
import org.springframework.stereotype.Component;

import java.util.function.Supplier;

@Component
public class ListCacheFactory {
    <T> ListCache<T> createListCache(DataEntry de, Class<T> clazz){
        return null;
    }

    <T> ListCache<T> createListCache(DataEntry de, Supplier<T> s){
        return null;
    }

}
