package com.boring.dal.dao;

import java.util.List;

public interface CollectionDao {
    List<Object[]> getDataListMulti(String listName, Object[] params, Integer start, Integer count, boolean forceMaster) throws Exception;

    <T> List<T> getDataListSingle(String listName, Object[] params, Integer start, Integer count, Integer colIdx, boolean forceMaster) throws Exception;

    Integer countDataList(String listName, Object[] params, boolean forceMaster) throws Exception;

    <T> T getDataMapSingle(String mapName, Object[] params, Integer colIdx, boolean forceMaster) throws Exception;

    Object[] getDataMapMulti(String mapName, Object[] params, boolean forceMaster) throws Exception;
}
