package com.boring.dal.dao.impl.jta;

import com.boring.dal.config.DataAccessConfig;
import com.boring.dal.dao.EntityDao;
import com.boring.dal.json.GsonUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.persistence.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Component
@Transactional(propagation = Propagation.REQUIRED)
public class JTAEntityImpl implements EntityDao {

    private static final Logger logger = LogManager.getLogger("DAO");

    @PersistenceContext
    private EntityManager entityManager;

    @Resource
    private DataAccessConfig dataAccessConfig;

    @Override
    public <T> T get(Object id, Class<T> clazz) throws Exception {
        if (id == null)
            return null;

        T obj = entityManager.find(clazz, id);

        if (logger.isDebugEnabled())
            logger.debug("get:" + id + ", result:" + GsonUtil.toJson(obj));
        return obj;
    }

    @Override
    public Object get(Object id, String clazz) throws Exception {
        if (id == null)
            return null;
        Class c= Class.forName(clazz);
        return get(id,c);
    }

    @Override
    public Object save(Object obj) throws Exception {
        if (obj == null)
            return null;
        entityManager.persist(obj);
        entityManager.flush();
        Method idgetter = dataAccessConfig.getEntityIdValGetter(obj);
        Object id = idgetter.invoke(obj);
        entityManager.detach(obj);
        if (logger.isDebugEnabled())
            logger.debug("save:" + GsonUtil.toJson(obj));
        return id;
    }

    @Override
    public void update(Object obj) throws Exception {
        if (obj == null)
            return;
        entityManager.merge(obj);
        entityManager.flush();
        entityManager.detach(obj);
        if (logger.isDebugEnabled())
            logger.debug("update:" + GsonUtil.toJson(obj));
    }

    @Override
    public List batchSave(List objList) throws Exception {
        if (objList == null)
            return null;
        ArrayList ids = new ArrayList<>(objList.size());
        for (Iterator iterator = objList.iterator(); iterator.hasNext(); ) {
            Object o = iterator.next();
            Object id = save(o);
            ids.add(id);
        }
        if (logger.isDebugEnabled())
            logger.debug("batchSave:" + GsonUtil.toJson(ids));
        return ids;
    }

    @Override
    public void delete(Object id, String clazz) throws Exception {
        Class cls= Class.forName(clazz);
        Table table = (Table) cls.getAnnotation(Table.class);
        Field idfield = dataAccessConfig.getEntityIdField(cls);
        Column idcol = idfield.getAnnotation(Column.class);
        String col = StringUtils.isEmpty(idcol.name()) ? idfield.getName() : idcol.name();
        if(idfield!=null){
            Query del = entityManager.createNativeQuery("delete from " + table.name() + " where " + col + "= :p0");
            del.setParameter("p0",id);
            del.executeUpdate();
        }
    }

    @Override
    public <T> List<T> batchGet(List idList, Class<T> clazz) throws Exception {
        idList.getClass().getGenericSuperclass();
        Table table = clazz.getAnnotation(Table.class);
        Field idfield = dataAccessConfig.getEntityIdField(clazz);
        Column id = idfield.getAnnotation(Column.class);
        String col = StringUtils.isEmpty(id.name()) ? idfield.getName() : id.name();
        Query query = entityManager.createNativeQuery("select * from " + table.name() + " where " + col + " in (:p0)",clazz);
        query.setParameter("p0", idList);
        List ret = query.getResultList();
        if (logger.isDebugEnabled())
            logger.debug("batchget:" + clazz.getTypeName() + "|", idList.size() + ", result:" + ret.size());
        return ret;
    }

    @Override
    public <T> List<T> batchGet(List idList, String clazz) throws Exception {
        Class t = Class.forName(clazz);
        return batchGet(idList, t);
    }


}
