package com.boring.dal.cache;

import com.boring.dal.cache.construct.VersionedValue;
import com.boring.dal.config.DataEntry;

import java.util.List;
import java.util.function.Supplier;

public interface RealmCache {

    void invalidateListData(DataEntry de, String key);

    void invalidateEntity(Object id, Object entity);

    int inspectList(DataEntry de, String key);

    void setListData(String listName, String key, Object data);

    <T> T getListData(String listName, String key, Supplier<T> s);

    boolean setVersionedListData(String listName, String key, VersionedValue data);

    <T> VersionedValue<T> getVersionedListData(String listName, String key, Supplier<T> s);

    <T> T getEntity(String id, Class<T> tClass);

    void updateEntity(String id, Object entity, Object oldEntity);

    void updateEntityIfNotPresent(String id, Object entity);

    void saveNewEntity(String id, Object entity);

    <T> List<T> batchGetEntity(Class<T> clazz, List idList);
}
