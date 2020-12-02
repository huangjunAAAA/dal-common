package com.boring.dal.test.local;

import com.boring.dal.test.model.TCity2;

import java.util.List;

public interface TCity2RI {
    
    TCity2 get(Integer id_0) throws Exception;
 
    List<TCity2> batchGet(List idList_0) throws Exception;
    
    void update(TCity2 tcity2_0) throws Exception;
    
    Integer save(TCity2 tcity2_0) throws Exception;
    
    List<Integer> batchSave(List tcity2List_0) throws Exception;

    Integer countTCountryCityList1(String country0_0, String city1_1) throws Exception;

    List<Object[]> listAllTCountryCityList1(String country0_0, String city1_1, Integer start_2, Integer count_3) throws Exception;

    List<Integer> listTCountryCityList1Forcity_id0(String country0_0, String city1_1, Integer start_2, Integer count_3) throws Exception;

    List<Integer> listTCountryCityList1Forcountry_id1(String country0_0, String city1_1, Integer start_2, Integer count_3) throws Exception;

    List listEntityTCountryCityList1(String country0_0, String city1_1, Integer start_2, Integer count_3, Class clazz_4) throws Exception;

    Object[] mapAllTCountryCitymap1(Integer id0_0) throws Exception;

    Integer mapTCountryCitymap1Forcity_id0(Integer id0_0) throws Exception;

    String mapTCountryCitymap1Forcity1(Integer id0_0) throws Exception;

    String mapTCountryCitymap2Forcity0(Integer id0_0) throws Exception;

    String mapTCountryCityList2Forcity0(Integer countryid0_0) throws Exception;
}