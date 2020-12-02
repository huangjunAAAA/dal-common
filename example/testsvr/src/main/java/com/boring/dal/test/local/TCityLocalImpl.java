package com.boring.dal.test.local;

import com.boring.dal.cache.ComprehensiveDao;
import com.boring.dal.test.model.TCity2;

import javax.annotation.Resource;
import java.util.List;

public class TCityLocalImpl implements TCity2RI {

    @Resource
    private ComprehensiveDao comprehensiveDao;

    @Override
    public TCity2 get(Integer id_0) throws Exception {
        return comprehensiveDao.get(id_0,TCity2.class);
    }

    @Override
    public List<TCity2> batchGet(List idList_0) throws Exception {
        return comprehensiveDao.batchGet(idList_0,TCity2.class);
    }

    @Override
    public void update(TCity2 tcity2_0) throws Exception {
        comprehensiveDao.update(tcity2_0);
    }

    @Override
    public Integer save(TCity2 tcity2_0) throws Exception {
        return (Integer) comprehensiveDao.save(tcity2_0);
    }

    @Override
    public List<Integer> batchSave(List tcity2List_0) throws Exception {
        return batchSave(tcity2List_0);
    }

    @Override
    public Integer countTCountryCityList1(String country0_0, String city1_1) throws Exception {
        return comprehensiveDao.countDataList("",new Object[]{city1_1});
    }

    @Override
    public List<Object[]> listAllTCountryCityList1(String country0_0, String city1_1, Integer start_2, Integer count_3) throws Exception {
        return comprehensiveDao.getDataListMulti("",new Object[]{country0_0,city1_1},start_2,count_3);
    }

    @Override
    public List<Integer> listTCountryCityList1Forcity_id0(String country0_0, String city1_1, Integer start_2, Integer count_3) throws Exception {
        return null;
    }

    @Override
    public List<Integer> listTCountryCityList1Forcountry_id1(String country0_0, String city1_1, Integer start_2, Integer count_3) throws Exception {
        return null;
    }

    @Override
    public List listEntityTCountryCityList1(String country0_0, String city1_1, Integer start_2, Integer count_3, Class clazz_4) throws Exception {
        return null;
    }

    @Override
    public Object[] mapAllTCountryCitymap1(Integer id0_0) throws Exception {
        return new Object[0];
    }

    @Override
    public Integer mapTCountryCitymap1Forcity_id0(Integer id0_0) throws Exception {
        return null;
    }

    @Override
    public String mapTCountryCitymap1Forcity1(Integer id0_0) throws Exception {
        return null;
    }

    @Override
    public String mapTCountryCitymap2Forcity0(Integer id0_0) throws Exception {
        return null;
    }

    @Override
    public String mapTCountryCityList2Forcity0(Integer countryid0_0) throws Exception {
        return null;
    }
}
