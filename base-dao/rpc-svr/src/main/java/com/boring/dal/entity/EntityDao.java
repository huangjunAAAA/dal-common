package com.boring.dal.entity;

import java.io.Serializable;
import java.util.List;

public interface EntityDao<T> {
    T get(Serializable id) throws Exception;

    Serializable save(Object obj) throws Exception;

    void update(Object obj) throws Exception;

    List<Serializable> batchSave(List objList) throws Exception;

    List<T> batchGet(List idList) throws Exception;
}
