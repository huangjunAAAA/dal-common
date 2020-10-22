package com.boring.dal.cache;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

public interface SimpleCache {
    String NullObject = "NULL_OBJECT";

    <T> T get(String region, String key, Supplier<T> supplier);

    <T> T get(String region, String key, Class<T> tClass);

    <T> T getEntity(String key, Class<T> tClass);

    void setEntity(String key, Object obj);

    void setEntity(String key, Object obj, int expire);

    void setRaw(String region, String key, Object content, int expire);

    void casEntity(String key, Object obj, Object expected);

    void casEntity(String key, Object obj, int expire, Object expected);

    void casRaw(String region, String key, Object content, int expire, Object expected);

    long incr(String region, String key, long delta);

    long decr(String region, String key, long delta);

    <T> List<T> batchGetEntity(Class<T> clazz, List idList);

    void deleteKey(String region, String key);

    class CacheKey {
        public final String region;
        public final String key;
        public CacheKey(String region, String key) {
            this.region = region;
            this.key = key;
        }

        @Override
        public int hashCode() {
            return region.hashCode() + key.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof CacheKey))
                return false;
            CacheKey cacheKey = (CacheKey) obj;
            return Arrays.equals(new String[]{region, key}, new String[]{cacheKey.region, cacheKey.key});
        }
    }
}
