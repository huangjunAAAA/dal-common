package com.boring.dal.dao.impl.jta;

import com.boring.dal.config.DataAccessConfig;
import com.boring.dal.config.DataEntry;
import com.boring.dal.config.SQLVarInfo;
import com.boring.dal.dao.CollectionDao;
import com.boring.dal.json.GsonUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.shardingsphere.api.hint.HintManager;
import org.hibernate.Session;
import org.hibernate.TypeHelper;
import org.hibernate.query.NativeQuery;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.*;
import java.util.function.Supplier;

@Component
@Transactional(propagation = Propagation.REQUIRED)
public class JTACollectionImpl implements CollectionDao {

    private static final Logger logger = LogManager.getLogger("DAO");

    @Resource
    private DataAccessConfig dataAccessConfig;

    @PersistenceContext
    private EntityManager entityManager;

    private static void setParameter(NativeQuery query, Object[] params, Integer start, Integer count) {
        if (null != params && params.length > 0) {
            int i = 0;
            int p = 0;
            boolean namedp = false;
            for (Object variant : params) {
                if (variant instanceof Collection) {
                    namedp = true;
                } else if (variant instanceof Object[]) {
                    namedp = true;
                }
            }
            for (Object id : params) {
                if (!namedp) {
                    query.setParameter(i++, id);
                } else if (id instanceof Collection) {
                    String np = "p" + p++;
                    query.setParameterList(np, (Collection) id);
                } else if (id instanceof Object[]) {
                    String np = "p" + p++;
                    query.setParameterList(np, (Object[]) id);
                } else
                    query.setParameter("p" + p++, id);
            }
        }
        if (start != null)
            query.setFirstResult(start.intValue());
        if (count != null)
            query.setMaxResults(count.intValue());
    }

    private static void setQueryScalar(NativeQuery query, DataEntry de, TypeHelper typeHelper) {
        for (Iterator<Map.Entry<String, Class>> iterator = de.getValueProperties().entrySet().iterator(); iterator.hasNext(); ) {
            Map.Entry<String, Class> val = iterator.next();
            query.addScalar(val.getKey(), typeHelper.basic(val.getValue()));
        }
    }

    private static void setIndexedQueryScalar(NativeQuery query, DataEntry de, TypeHelper typeHelper, Integer colIdx) {
        for (Iterator<Map.Entry<String, Class>> iterator = de.getValueProperties().entrySet().iterator(); iterator.hasNext(); ) {
            Map.Entry<String, Class> val = iterator.next();
            if (colIdx == null || colIdx == 0) {
                query.addScalar(val.getKey(), typeHelper.basic(val.getValue()));
                return;
            } else {
                colIdx--;
            }
        }
    }

    @Override
    public List<Object[]> getDataListMulti(String listName, Object[] params, Integer start, Integer count, boolean forceMaster) throws Exception {
        DataEntry de = validateDataEntryParam(listName, params);
        if (de.getValueProperties().size() == 1)
            throw new Exception("Please use getDataListSingle for Data Entry [" + listName + "] instead.");
        if (logger.isDebugEnabled())
            logger.debug("getDataListMulti:" + listName + ", params:" + Arrays.toString(params) + ", start:" + start + ", count:" + count + ", master:" + forceMaster);
        Session ses = entityManager.unwrap(Session.class);
        NativeQuery query = ses.createSQLQuery(de.getSql());
        setParameter(query, params, start, count);
        setQueryScalar(query, de, ses.getTypeHelper());
        List ret = queryDB(() -> query.list(), forceMaster);
        if (logger.isDebugEnabled())
            logger.debug("getDataListMulti:" + listName + ", params:" + Arrays.toString(params) + ", start:" + start + ", count:" + count + ", master:" + forceMaster + ", result:" + ret.size());
        return ret;
    }

    private <T> T queryDB(Supplier<T> func, boolean forceMaster) {
        boolean ma = HintManager.isMasterRouteOnly();
        if (!ma && forceMaster) {
            HintManager.getInstance().setMasterRouteOnly();
        }
        T ret = func.get();
        if (!ma && forceMaster) {
            HintManager.clear();
        }
        return ret;
    }

    @Override
    public <T> List<T> getDataListSingle(String listName, Object[] params, Integer start, Integer count, Integer colIdx, boolean forceMaster) throws Exception {
        DataEntry de = validateDataEntryParam(listName, params);
        if (logger.isDebugEnabled())
            logger.debug("getDataListSingle:" + listName + ", params:" + Arrays.toString(params) + ", start:" + start + ", count:" + count + ", colIdx:" + colIdx + ", master:" + forceMaster);
        Session ses = entityManager.unwrap(Session.class);
        NativeQuery<T> query = ses.createSQLQuery(de.getSql());
        setParameter(query, params, start, count);
        setIndexedQueryScalar(query, de, ses.getTypeHelper(), colIdx);
        List ret = queryDB(() -> query.list(), forceMaster);
        if (logger.isDebugEnabled())
            logger.debug("getDataListSingle:" + listName + ", params:" + Arrays.toString(params) + ", start:" + start + ", count:" + count + ", colIdx:" + colIdx + ", master:" + forceMaster + ", result:" + ret.size());
        return ret;
    }

    private DataEntry validateDataEntryParam(String listName, Object[] param) throws Exception {
        DataEntry de = dataAccessConfig.getDataEntryByName(listName);
        if (de == null)
            throw new Exception("Data Entry Not Found:" + listName);
        LinkedList<SQLVarInfo> kprops = de.getKeyProperties();
        int plength = param == null ? 0 : param.length;
        if (plength != kprops.size()) {
            throw new Exception("Data Entry [" + listName + "] Params Number Mismatched, Required:" + kprops.size() + ", Found:" + plength);
        }
        if (param != null) {
            for (int i = 0; i < param.length; i++) {
                SQLVarInfo km = kprops.get(i);
                if (km == null || km.getter == null)
                    continue;
                if (param[i] == null) {
                    throw new Exception("Data Entry [" + listName + "] Param " + i + " is Null");
                }

                Class<?> pClass = param[i].getClass();
                Class<?> targetClass = km.getter.getReturnType();
                if (km.isCollection) {
                    String tn = pClass.getTypeName();
                    if (tn.indexOf("[]") >= 0) {
                        pClass = Class.forName(tn.replace("[]", ""));
                    } else {
                        throw new Exception("Data Entry [" + listName + "] Param " + i + " Type Require Collection");
                    }
                }
                if (!targetClass.isAssignableFrom(pClass)) {
                    logger.info("Data Entry [" + listName + "] Param " + i + " Type Mismatched, Require:" + targetClass.getTypeName() + ", Found:" + pClass.getTypeName());
                }
            }
        }
        return de;
    }

    @Override
    public Integer countDataList(String listName, Object[] params, boolean forceMaster) throws Exception {
        DataEntry de = validateDataEntryParam(listName, params);
        if (logger.isDebugEnabled())
            logger.debug("countDataList:" + listName + ", params:" + Arrays.toString(params) + ", master:" + forceMaster);
        Session ses = entityManager.unwrap(Session.class);
        NativeQuery query = ses.createSQLQuery("select count(1) cc from (" + de.getSql() + ") clst");
        setParameter(query, params, null, null);
        query.addScalar("cc", ses.getTypeHelper().basic(Integer.class));
        Integer ret = queryDB(() -> (Integer) query.uniqueResult(), forceMaster);
        if (logger.isDebugEnabled())
            logger.debug("countDataList:" + listName + ", params:" + Arrays.toString(params) + ", master:" + forceMaster + ", result:" + ret);
        return ret;
    }

    @Override
    public <T> T getDataMapSingle(String mapName, Object[] params, Integer colIdx, boolean forceMaster) throws Exception {
        DataEntry de = validateDataEntryParam(mapName, params);
        if (logger.isDebugEnabled())
            logger.debug("getDataMapSingle:" + mapName + ", params:" + Arrays.toString(params) + ", colIdx:" + colIdx + ", master:" + forceMaster);
        Session ses = entityManager.unwrap(Session.class);
        NativeQuery<T> query = ses.createSQLQuery(de.getSql());
        setParameter(query, params, null, null);
        setIndexedQueryScalar(query, de, ses.getTypeHelper(), colIdx);
        T ret = queryDB(() -> query.uniqueResult(), forceMaster);
        if (logger.isDebugEnabled())
            logger.debug("getDataMapSingle:" + mapName + ", params:" + Arrays.toString(params) + ", colIdx:" + colIdx + ", master:" + forceMaster + ", result:" + GsonUtil.toJson(ret));
        return ret;
    }

    @Override
    public Object[] getDataMapMulti(String mapName, Object[] params, boolean forceMaster) throws Exception {
        DataEntry de = validateDataEntryParam(mapName, params);
        if (de.getValueProperties().size() == 1)
            throw new Exception("Please use getDataMapSingle for Data Entry [" + mapName + "] instead.");
        if (logger.isDebugEnabled())
            logger.debug("getDataMapMulti:" + mapName + ", params:" + Arrays.toString(params) + ", master:" + forceMaster);
        if (forceMaster)
            HintManager.getInstance().setMasterRouteOnly();
        Session ses = entityManager.unwrap(Session.class);
        NativeQuery query = ses.createSQLQuery(de.getSql());
        setParameter(query, params, null, null);
        setQueryScalar(query, de, ses.getTypeHelper());
        Object[] ret = queryDB(() -> (Object[]) query.uniqueResult(), forceMaster);
        if (logger.isDebugEnabled())
            logger.debug("getDataMapMulti:" + mapName + ", params:" + Arrays.toString(params) + ", master:" + forceMaster + ", result:" + Arrays.toString(ret));
        return ret;
    }
}
