package com.boring.dal.cache;

import com.boring.dal.cache.construct.VersionedValue;

import java.util.List;
import java.util.function.Supplier;

public interface SimpleCache {
    String NullObject = "NULL_OBJECT";

    <T> T get(String region, String key, Supplier<T> supplier);

    <T> T get(String region, String key, Class<T> tClass);

    <T> T getEntity(String key, Class<T> tClass);

    void setEntity(String key, Object obj);

    void setEntity(String key, Object obj, int expire);

    <T> VersionedValue<T> getVersionedObject(String region, String key, Supplier<T> supplier);

    <T> VersionedValue<T> getVersionedObject(String region, String key, Class<T> tClass);

    <T> VersionedValue<T> getVersionedEntity(String key, Class<T> tClass);

    boolean setVersionedEntity(String key, VersionedValue obj);

    boolean setVersionedEntity(String key, VersionedValue obj, int expire);

    boolean setVersionedRaw(String region, String key, VersionedValue content, int expire);

    void setRaw(String region, String key, Object content, int expire);

    long incr(String region, String key, long delta);

    long decr(String region, String key, long delta);

    <T> List<T> batchGetEntity(Class<T> clazz, List idList);

    void deleteKey(String region, String key);

    void setIfNotPresent(String region,String key, Object content, int expire);

}
