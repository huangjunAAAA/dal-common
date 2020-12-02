package com.boring.test.rpc;

import java.util.*;
import com.boring.dal.remote.autorpc.AutoRpc;
import com.boring.dal.remote.autorpc.RemoteAccess;
import com.boring.dal.test.model.TCity2;

@AutoRpc
public interface TCity2RI{

    @RemoteAccess
    TCity2 get(java.lang.Integer id_0) throws java.lang.Exception;
    @RemoteAccess
    List<TCity2> batchGet(java.util.List idList_0) throws java.lang.Exception;
    @RemoteAccess
    void update(com.boring.dal.test.model.TCity2 tcity2_0) throws java.lang.Exception;
    @RemoteAccess
    java.lang.Integer save(com.boring.dal.test.model.TCity2 tcity2_0) throws java.lang.Exception;
    @RemoteAccess
    List<java.lang.Integer> batchSave(java.util.List tcity2List_0) throws java.lang.Exception;
    @RemoteAccess(targetList="TCountryCity_List1", remote="countDataList")
    Integer countTCountryCity_List1(java.lang.String country0_0,java.lang.String city1_1) throws java.lang.Exception;
    @RemoteAccess(targetList="TCountryCity_List1", remote="getDataListMulti")
    List<Object[]> listAllTCountryCity_List1(java.lang.String country0_0,java.lang.String city1_1,java.lang.Integer start_2,java.lang.Integer count_3) throws java.lang.Exception;
    @RemoteAccess(targetList="TCountryCity_List1", remote="getDataListSingle", idx=0)
    List<java.lang.Integer> listTCountryCity_List1Forcity_id0(java.lang.String country0_0,java.lang.String city1_1,java.lang.Integer start_2,java.lang.Integer count_3) throws java.lang.Exception;
    @RemoteAccess(targetList="TCountryCity_List1", remote="getDataListSingle", idx=1)
    List<java.lang.Integer> listTCountryCity_List1Forcountry_id1(java.lang.String country0_0,java.lang.String city1_1,java.lang.Integer start_2,java.lang.Integer count_3) throws java.lang.Exception;
    @RemoteAccess(targetList="TCountryCity_List1", remote="getDataListEntity")
    List listEntityTCountryCity_List1(java.lang.String country0_0,java.lang.String city1_1,java.lang.Integer start_2,java.lang.Integer count_3,java.lang.Class clazz_4) throws java.lang.Exception;
    @RemoteAccess(targetList="TCountryCity_map1", remote="getDataMapMulti")
    Object[] mapAllTCountryCity_map1(java.lang.Integer id0_0) throws java.lang.Exception;
    @RemoteAccess(targetList="TCountryCity_map1", remote="getDataMapSingle", idx=0)
    java.lang.Integer mapTCountryCity_map1Forcity_id0(java.lang.Integer id0_0) throws java.lang.Exception;
    @RemoteAccess(targetList="TCountryCity_map1", remote="getDataMapSingle", idx=1)
    java.lang.String mapTCountryCity_map1Forcity1(java.lang.Integer id0_0) throws java.lang.Exception;
    @RemoteAccess(targetList="TCountryCity_map2", remote="getDataMapSingle", idx=0)
    java.lang.String mapTCountryCity_map2Forcity0(java.lang.Integer id0_0) throws java.lang.Exception;
    @RemoteAccess(targetList="TCountryCity_List2", remote="getDataMapSingle", idx=0)
    java.lang.String mapTCountryCity_List2Forcity0(java.lang.Integer countryid0_0) throws java.lang.Exception;


}