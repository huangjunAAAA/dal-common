package com.boring.dal.cache;

import com.boring.dal.dao.EntityDao;

import java.util.List;

public interface ComprehensiveDao extends EntityDao {
    List<Object[]> getDataListMulti(String listName, Object[] params, Integer start, Integer count) throws Exception;

    <T> List<T> getDataListSingle(String listName, Object[] params, Integer start, Integer count, Integer colIdx) throws Exception;

    Integer countDataList(String listName, Object[] params) throws Exception;

    <T> T getDataMapSingle(String mapName, Object[] params, Integer colIdx) throws Exception;

    Object[] getDataMapMulti(String mapName, Object[] params) throws Exception;

    <E> List<E> getDataListEntity(String listName, Object[] params, Integer start, Integer count, Class<E> clazz, Integer colIdx) throws Exception;

    List getDataListEntity(String listName, Object[] params, Integer start, Integer count, String clazz, Integer colIdx) throws Exception;
}
