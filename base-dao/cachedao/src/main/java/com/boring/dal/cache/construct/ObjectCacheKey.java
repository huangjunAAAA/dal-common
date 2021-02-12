package com.boring.dal.cache.construct;

import java.util.Arrays;

public class ObjectCacheKey {

    public final String region;
    public final String key;
    public ObjectCacheKey(String region, String key) {
        this.region = region;
        this.key = key;
    }

    @Override
    public int hashCode() {
        return region.hashCode() + key.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ObjectCacheKey))
            return false;
        ObjectCacheKey cacheKey = (ObjectCacheKey) obj;
        return Arrays.equals(new String[]{region, key}, new String[]{cacheKey.region, cacheKey.key});
    }
}
