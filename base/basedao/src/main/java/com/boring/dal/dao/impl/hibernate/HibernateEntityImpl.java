package com.boring.dal.dao.impl.hibernate;

import com.boring.dal.config.DataAccessConfig;
import com.boring.dal.dao.EntityDao;
import com.boring.dal.json.GsonUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.NativeQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.persistence.Column;
import javax.persistence.Query;
import javax.persistence.Table;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Component
@Transactional(propagation = Propagation.REQUIRED)
public class HibernateEntityImpl implements EntityDao {

    private static final Logger logger = LogManager.getLogger("DAO");

    @Autowired
    private SessionFactory sessionFactory;

    @Resource
    private DataAccessConfig dataAccessConfig;

    @Override
    public <T> T get(Object id, Class<T> clazz) throws Exception {
        if (id == null)
            return null;
        Session ses = sessionFactory.getCurrentSession();

        T obj = ses.get(clazz, (Serializable) id);

        if (logger.isDebugEnabled())
            logger.debug("get:" + id + ", result:" + GsonUtil.toJson(obj));
        return obj;
    }

    @Override
    public Object get(Object id, String clazz) throws Exception {
        if (id == null)
            return null;
        Session ses = sessionFactory.getCurrentSession();
        Object obj = ses.get(clazz, (Serializable) id);
        if (logger.isDebugEnabled())
            logger.debug("get:" + id + ", result:" + GsonUtil.toJson(obj));
        return obj;
    }

    @Override
    public Object save(Object obj) throws Exception {
        if (obj == null)
            return null;
        Session ses = sessionFactory.getCurrentSession();
        Serializable id = ses.save(obj);
        if (logger.isDebugEnabled())
            logger.debug("save:" + GsonUtil.toJson(obj));
        return id;
    }

    @Override
    public void update(Object obj) throws Exception {
        if (obj == null)
            return;
        Session ses = sessionFactory.getCurrentSession();
        ses.update(obj);
        if (logger.isDebugEnabled())
            logger.debug("update:" + GsonUtil.toJson(obj));
    }

    @Override
    public List batchSave(List objList) throws Exception {
        if (objList == null)
            return null;
        Session ses = sessionFactory.getCurrentSession();
        ArrayList<Serializable> ids = new ArrayList<>(objList.size());
        for (Iterator iterator = objList.iterator(); iterator.hasNext(); ) {
            Object o = iterator.next();
            Serializable id = ses.save(o);
            ids.add(id);
        }
        if (logger.isDebugEnabled())
            logger.debug("batchSave:" + GsonUtil.toJson(ids));
        return ids;
    }

    @Override
    public void delete(Object id, String clazz) throws Exception {
        Session ses = sessionFactory.getCurrentSession();
        Class cls= Class.forName(clazz);
        Table table = (Table) cls.getAnnotation(Table.class);
        Field idfield = dataAccessConfig.getEntityIdField(cls);
        Column idcol = idfield.getAnnotation(Column.class);
        String col = StringUtils.isEmpty(idcol.name()) ? idfield.getName() : idcol.name();
        if(idfield!=null){
            NativeQuery del = ses.createNativeQuery("delete from " + table.name() + " where " + col + "= :p0");
            del.setParameter("p0",id);
            del.executeUpdate();
        }
    }

    @Override
    public <T> List<T> batchGet(List idList, Class<T> clazz) throws Exception {
        idList.getClass().getGenericSuperclass();
        Session ses = sessionFactory.getCurrentSession();
        Table table = clazz.getAnnotation(Table.class);
        Field idfield = dataAccessConfig.getEntityIdField(clazz);
        Column id = idfield.getAnnotation(Column.class);
        String col = StringUtils.isEmpty(id.name()) ? idfield.getName() : id.name();
        NativeQuery query = ses.createNativeQuery("select * from " + table.name() + " where " + col + " in (:p0)");
        query.addEntity(clazz);
        query.setParameter("p0", idList);
        List ret = query.list();
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
