package com.boring.test.local;

import com.boring.dal.cache.ComprehensiveDao;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

@Component
public class TCity2LocalImpl{

    @Resource
    private ComprehensiveDao comprehensiveDao;

    
    public com.boring.dal.test.model.TCity2 get(java.lang.Integer id) throws Exception {
        return comprehensiveDao.get(id,com.boring.dal.test.model.TCity2.class);
    }

    
    public List<com.boring.dal.test.model.TCity2> batchGet(List objLst) throws Exception {
        return comprehensiveDao.batchGet(objLst,com.boring.dal.test.model.TCity2.class);
    }

    
    public void update(com.boring.dal.test.model.TCity2 obj) throws Exception {
        comprehensiveDao.update(obj);
    }

    
    public java.lang.Integer save(com.boring.dal.test.model.TCity2 obj) throws Exception {
        return (java.lang.Integer) comprehensiveDao.save(obj);
    }

    
    public List<java.lang.Integer> batchSave(List objLst) throws Exception {
        return batchSave(objLst);
    }
    

    

    

    

    

    Integer countTCountryCity_List1(java.lang.String country0_0,java.lang.String city1_1) throws java.lang.Exception{
        return comprehensiveDao.countDataList("TCountryCity_List1",new Object[]{country0_0,city1_1});
    }

    List<com.boring.test.local.construct.Ret4TCountryCity_List1> listAllTCountryCity_List1(java.lang.String country0_0,java.lang.String city1_1,java.lang.Integer start_2,java.lang.Integer count_3) throws java.lang.Exception{
        return com.boring.test.local.construct.Ret4TCountryCity_List1.fromObjectArrayList( comprehensiveDao.getDataListMulti("TCountryCity_List1",new Object[]{country0_0,city1_1},start_2,count_3));
    }

    List<java.lang.Integer> listTCountryCity_List1Forcity_id0(java.lang.String country0_0,java.lang.String city1_1,java.lang.Integer start_2,java.lang.Integer count_3) throws java.lang.Exception{
        return comprehensiveDao.getDataListSingle("TCountryCity_List1",new Object[]{country0_0,city1_1},start_2,count_3,0);
    }

    List<java.lang.Integer> listTCountryCity_List1Forcountry_id1(java.lang.String country0_0,java.lang.String city1_1,java.lang.Integer start_2,java.lang.Integer count_3) throws java.lang.Exception{
        return comprehensiveDao.getDataListSingle("TCountryCity_List1",new Object[]{country0_0,city1_1},start_2,count_3,1);
    }

    List listEntityTCountryCity_List1(java.lang.String country0_0,java.lang.String city1_1,java.lang.Integer start_2,java.lang.Integer count_3,java.lang.Class clazz_4) throws java.lang.Exception{
        return comprehensiveDao.getDataListEntity("TCountryCity_List1",new Object[]{country0_0,city1_1},start_2,count_3,clazz_4,0);
    }

    com.boring.test.local.construct.Ret4TCountryCity_map1 mapAllTCountryCity_map1(java.lang.Integer id0_0) throws java.lang.Exception{
        return com.boring.test.local.construct.Ret4TCountryCity_map1.fromObjectArray( comprehensiveDao.getDataMapMulti("TCountryCity_map1",new Object[]{id0_0}));
    }

    java.lang.Integer mapTCountryCity_map1Forcity_id0(java.lang.Integer id0_0) throws java.lang.Exception{
        return comprehensiveDao.getDataMapSingle("TCountryCity_map1",new Object[]{id0_0},0);
    }

    java.lang.String mapTCountryCity_map1Forcity1(java.lang.Integer id0_0) throws java.lang.Exception{
        return comprehensiveDao.getDataMapSingle("TCountryCity_map1",new Object[]{id0_0},1);
    }

    java.lang.String mapTCountryCity_map2Forcity0(java.lang.Integer id0_0) throws java.lang.Exception{
        return comprehensiveDao.getDataMapSingle("TCountryCity_map2",new Object[]{id0_0},0);
    }

    java.lang.String mapTCountryCity_List2Forcity0(java.lang.Integer countryid0_0) throws java.lang.Exception{
        return comprehensiveDao.getDataMapSingle("TCountryCity_List2",new Object[]{countryid0_0},0);
    }

}