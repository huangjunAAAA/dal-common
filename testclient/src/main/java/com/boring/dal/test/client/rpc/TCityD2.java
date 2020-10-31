package com.boring.dal.test.client.rpc;

import com.boring.dal.remote.autorpc.AutoRpc;
import com.boring.dal.remote.autorpc.RemoteAccess;
import com.boring.dal.test.model.TCity2;

import java.util.List;

@AutoRpc
public interface TCityD2 {

    @RemoteAccess
    TCity2 get(Object id) throws Exception;

    @RemoteAccess
    List<TCity2> batchGet(List idlst) throws Exception;

    @RemoteAccess
    void update(TCity2 city) throws Exception;

    @RemoteAccess
    Integer save(TCity2 city) throws Exception;

    @RemoteAccess
    List<Integer> batchSave(List citylst) throws Exception;

    @RemoteAccess(targetList = "TCountryCity_List1",remote = "getDataListMulti")
    List<Object[]> getTCountryCity_List1(String countryName, String cityName, Integer start, Integer count) throws Exception;

    @RemoteAccess(targetList = "TCountryCity_List1",remote = "getDataListSingle",idx = 0)
    List<String> getTCountryCity_List1Forcity_id(String countryName, String cityName, Integer start, Integer count) throws Exception;

    @RemoteAccess(targetList = "TCountryCity_List2",remote = "getDataListMulti")
    List<Object[]> getTCountryCity_List2(Integer countryId, Integer start, Integer count) throws Exception;

    @RemoteAccess(targetList = "TCountryCity_List2",remote = "getDataListSingle",idx = 0)
    List<String> getTCountryCity_List2Forcity(Integer countryId, Integer start, Integer count) throws Exception;

    @RemoteAccess(targetList = "TCountryCity_List1",remote = "getDataListEntity",idx = 0)
    <T> List<T> getTCountryCity_List1Forcity_idEntity(String countryName, String cityName, Integer start, Integer count, Class<T> clazz) throws Exception;

    @RemoteAccess(targetList = "TCountryCity_List1",remote = "countDataList")
    Integer countTCountryCity_List1(String countryName, String cityName) throws Exception;

    @RemoteAccess(targetList = "TCountryCity_map1",remote = "getDataMapSingle",idx = 0)
    Integer getTCountryCity_Map1Forcity_id(Integer cityId) throws Exception;

    @RemoteAccess(targetList = "TCountryCity_map1",remote = "getDataMapMulti")
    Object[] getTCountryCity_Map1(Integer cityId) throws Exception;

    @RemoteAccess(targetList = "TCountryCity_map2",remote = "getDataMapSingle",idx = 0)
    String getTCountryCity_Map2Forcity(Integer cityId) throws Exception;

    @RemoteAccess(targetList = "TCountryCity_map2",remote = "getDataMapMulti")
    Object[] getTCountryCity_Map2(Integer cityId) throws Exception;
}
