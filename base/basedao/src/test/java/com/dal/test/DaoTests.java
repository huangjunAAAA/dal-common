package com.dal.test;


import com.boring.dal.ConfigConf;
import com.boring.dal.config.DataAccessConfig;
import com.boring.dal.dao.CollectionDao;
import com.boring.dal.dao.EntityDao;
import com.boring.dal.dao.HibernateConf;
import com.boring.dal.dao.impl.HibernateCollectionImpl;
import com.boring.dal.dao.impl.HibernateEntityImpl;
import com.dal.test.model.TActor;
import org.apache.shardingsphere.core.yaml.config.masterslave.YamlRootMasterSlaveConfiguration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {ConfigConf.class, HibernateConf.class, HibernateEntityImpl.class, HibernateCollectionImpl.class, HibernateJpaAutoConfiguration.class})
public class DaoTests {


    @Resource
    private DataAccessConfig dataAccessConfig;
    @Resource
    private DataSource dataSource;
    @Autowired
    private EntityDao entityDao;
    @Autowired
    private CollectionDao collectionDao;
    @Resource
    private YamlRootMasterSlaveConfiguration shardingConfig;

    @Test
    public void loadconfig() {
        dataAccessConfig.init();
    }

    @Test
    public void loadds() {
        System.out.println(dataSource);
    }

    @Test
    public void testGet() throws Exception {
        TActor ta1 = entityDao.get(1, TActor.class);
        System.out.println(ta1);
        System.out.println(shardingConfig);
    }

    @Test
    public void testBatchGet() throws Exception {
        List<Serializable> idlst = new ArrayList<>();
        idlst.add("1");
        idlst.add(3);
        idlst.add(7);
        List<TActor> xxl = entityDao.batchGet(idlst, TActor.class);
        System.out.println(xxl);
    }

    @Test
    public void testgetSingleList() throws Exception {
        List<Integer> id1 = collectionDao.getDataListSingle("TActor_List1", new Object[]{new String("NICK")}, 0, 1, null, false);
        System.out.println(id1);
    }

    @Test
    public void testgetSingleList2() throws Exception {
        List<String> id1 = collectionDao.getDataListSingle("TActor_List2", new Object[]{new Object[]{"1", 2, "3", "4"}}, 0, Integer.MAX_VALUE, null, false);
        System.out.println(id1);
    }

    @Test
    public void testgetSingleList3() throws Exception {
        List id1 = collectionDao.getDataListMulti("TActor_List3", new Object[]{new Integer[]{1, 2, 3, 4}}, 0, Integer.MAX_VALUE, false);
        Object[] r = (Object[]) id1.get(0);
        for (int i = 0; i < r.length; i++) {
            Object o = r[i];
            System.out.println(o.getClass());
        }
    }

    @Test
    public void testgetSingleMAP() throws Exception {
        String id1 = collectionDao.getDataMapSingle("TActor_Map1", new Object[]{5}, null, false);
        System.out.println(id1);
    }

    @Test
    public void testGetMultiMAP() throws Exception {
        Object[] id1 = collectionDao.getDataMapMulti("TActor_Map1", new Object[]{5}, false);
        System.out.println(Arrays.toString(id1));
    }

    @Test
    public void testcount() throws Exception {
        Integer id1 = collectionDao.countDataList("TCity_List4", null, false);
        System.out.println(id1);
    }

    @Test
    public void testDel() throws Exception{
        entityDao.delete(777,TActor.class.getTypeName());
    }

}
