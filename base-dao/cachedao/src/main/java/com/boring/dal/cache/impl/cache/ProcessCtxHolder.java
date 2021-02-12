package com.boring.dal.cache.impl.cache;

import com.boring.dal.cache.construct.ObjectCacheKey;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.ConcurrentHashMap;

public class ProcessCtxHolder {
    static final ConcurrentHashMap<ObjectCacheKey, Long> processDirty = new ConcurrentHashMap<>();
}
