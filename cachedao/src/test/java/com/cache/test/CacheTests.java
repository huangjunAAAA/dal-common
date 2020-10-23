package com.cache.test;


import com.boring.dal.cache.CacheHelper;
import com.boring.dal.cache.ComprehensiveDao;
import com.boring.dal.cache.CacheConf;
import com.boring.dal.cache.impl.DaoFacade;
import com.boring.dal.cache.impl.ProcessCache;
import com.boring.dal.cache.impl.XMemCache;
import com.boring.dal.cache.sanitizer.UrlEncodeSanitizer;
import com.boring.dal.config.DataAccessConfig;
import com.boring.dal.dao.HibernateConf;
import com.boring.dal.dao.impl.HibernateCollectionImpl;
import com.boring.dal.dao.impl.HibernateEntityImpl;
import com.boring.dal.json.GsonUtil;
import com.cache.test.model.TCity;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.List;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {DataAccessConfig.class, HibernateConf.class,
        HibernateEntityImpl.class, HibernateCollectionImpl.class, DaoFacade.class,
        CacheConf.class, XMemCache.class, CacheHelper.class, UrlEncodeSanitizer.class, ProcessCache.class})
public class CacheTests {

    @Autowired
    private CacheHelper cacheHelper;

    @Resource
    private DataAccessConfig dataAccessConfig;

    @Autowired
    private ComprehensiveDao comprehensiveDao;

    @Test
    public void loadconfig() {
        dataAccessConfig.init();
    }

    @Test
    public void testlist1() throws Exception {
        List<Object[]> citylist = comprehensiveDao.getDataListMulti("TCity_List4", new Object[]{23}, 0, 4);
        System.out.println("1111-"+GsonUtil.toJson(citylist));
        TCity t1 = comprehensiveDao.get(46, TCity.class);
        t1.setCountryId(23);
        comprehensiveDao.update(t1);

        Thread.sleep(150);
        List<Object[]> citylist1 = comprehensiveDao.getDataListMulti("TCity_List4", new Object[]{23}, 0, 4);
        System.out.println("1112-"+GsonUtil.toJson(citylist1));
    }

    @Test
    public void testlistcount() throws Exception {
        Integer c = comprehensiveDao.countDataList("TCity_List4", new Object[]{44});
        System.out.println(c);
    }

    @Test
    public void testentityget() throws Exception {
        TCity x = comprehensiveDao.get(16, TCity.class);
        Object id = cacheHelper.getObjectIdVal(x);
        Assert.assertEquals(id, 16);
    }

}
