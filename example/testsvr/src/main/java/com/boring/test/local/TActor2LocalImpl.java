package com.boring.test.local;

import java.util.*;
import com.boring.dal.cache.ComprehensiveDao;
import com.boring.dal.test.model.TActor2;
import javax.annotation.Resource;
import org.springframework.stereotype.Component;

@Component
public class TActor2LocalImpl{

    @Resource
    private ComprehensiveDao comprehensiveDao;

    
    public com.boring.dal.test.model.TActor2 get(java.lang.Integer id) throws Exception {
        return comprehensiveDao.get(id,com.boring.dal.test.model.TActor2.class);
    }

    
    public List<com.boring.dal.test.model.TActor2> batchGet(List objLst) throws Exception {
        return comprehensiveDao.batchGet(objLst,com.boring.dal.test.model.TActor2.class);
    }

    
    public void update(com.boring.dal.test.model.TActor2 obj) throws Exception {
        comprehensiveDao.update(obj);
    }

    
    public java.lang.Integer save(com.boring.dal.test.model.TActor2 obj) throws Exception {
        return (java.lang.Integer) comprehensiveDao.save(obj);
    }

    
    public List<java.lang.Integer> batchSave(List objLst) throws Exception {
        return batchSave(objLst);
    }
    
    
    
    
    
    
    
    
    
    
}