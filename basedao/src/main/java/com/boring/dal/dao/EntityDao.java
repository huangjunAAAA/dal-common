package com.boring.dal.dao;

import java.util.List;

public interface EntityDao {
    <T> T get(Object id, Class<T> clazz) throws Exception;

    Object get(Object id, String clazz) throws Exception;

    <T> List<T> batchGet(List idList, Class<T> clazz) throws Exception;

    <T> List<T> batchGet(List idList, String clazz) throws Exception;

    Object save(Object obj) throws Exception;

    void update(Object obj) throws Exception;

    List batchSave(List objList) throws Exception;

    void delete(Object id, String clazz) throws Exception;
}
