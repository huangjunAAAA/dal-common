package com.boring.dal.cache.impl;

import com.boring.dal.cache.SimpleCache;
import com.boring.dal.cache.sanitizer.KeySanitizer;
import com.boring.dal.json.GsonUtil;
import net.rubyeye.xmemcached.CASOperation;
import net.rubyeye.xmemcached.MemcachedClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;


public class XMemCache implements SimpleCache {

    private static final Logger logger = LogManager.getLogger("DAO");

    @Resource
    private MemcachedClient memcachedClient;

    @Autowired
    private KeySanitizer sanitizer;

    @Override
    public <T> T get(String region, String key, Supplier<T> supplier) {
        String key1 = sanitizer.sanitize(key);
        try {
            return memcachedClient.withNamespace(region, mc -> {
                T t = mc.get(key1);
                if (logger.isDebugEnabled())
                    logger.debug("cache get:" + region + "," + key + ", result:" + t);
                return t;
            });
        } catch (Exception e) {
            logger.error(e, e);
        }
        return null;
    }

    @Override
    public <T> T get(String region, String key, Class<T> tClass) {
        String key1 = sanitizer.sanitize(key);
        try {
            return memcachedClient.withNamespace(region, mc -> {
                T t = mc.get(key1);
                if (logger.isDebugEnabled())
                    logger.debug("cache get:" + region + "," + key + ", result:" + t);
                return t;
            });
        } catch (Exception e) {
            logger.error(e, e);
        }
        return null;
    }

    @Override
    public <T> T getEntity(String id, Class<T> tClass) {
        String key = sanitizer.sanitize(id);
        return get(tClass.getTypeName(), key, () -> null);
    }

    @Override
    public void setEntity(String key, Object obj) {
        setEntity(key, obj, 0);
    }

    @Override
    public void setEntity(String id, Object obj, int expire) {
        if (logger.isDebugEnabled())
            logger.debug("cache set:" + obj.getClass().getTypeName() + "," + id);
        setRaw(obj.getClass().getTypeName(), id, obj, expire);
    }

    @Override
    public void setRaw(String region, String key, Object content, int expire) {
        if (logger.isDebugEnabled())
            logger.debug("cache setraw:" + region + "," + key + ", content:" + (content instanceof String?content:GsonUtil.toJson(content)) + ", expire:" + expire);
        String key1 = sanitizer.sanitize(key);
        try {
            memcachedClient.withNamespace(region, mc -> {
                mc.setWithNoReply(key1, expire, content);
                return null;
            });
        } catch (Exception e) {
            logger.error(e, e);
        }
    }


    public void casEntity(String key, Object obj, Object expected) {
        casEntity(key, obj, 0, expected);
    }

    public void casEntity(String id, Object obj, int expire, Object expected) {
        casRaw(obj.getClass().getTypeName(), id, obj, expire, expected);
    }

    public void casRaw(String region, String key, Object content, int expire, Object expected) {
        if (logger.isDebugEnabled())
            logger.debug("cache cas:" + region + "," + key + ", content:" + content + ", expire:" + expire);
        String key1 = sanitizer.sanitize(key);
        try {
            memcachedClient.withNamespace(region, mc -> {
                mc.cas(key1, expire, new CASOperation<Object>() {
                    @Override
                    public int getMaxTries() {
                        return 1;
                    }

                    @Override
                    public Object getNewValue(long currentCAS, Object currentValue) {
                        if ((expected == null && currentValue == null) || expected.equals(currentValue))
                            return content;
                        return currentValue;
                    }
                });
                return null;
            });
        } catch (Exception e) {
            logger.error(e, e);
        }

    }

    @Override
    public long incr(String region, String key, long delta) {
        try {
            return memcachedClient.withNamespace(region, mc -> mc.incr(key, delta));
        } catch (Exception e) {
            logger.error(e, e);
        }
        return -1;
    }

    @Override
    public long decr(String region, String key, long delta) {
        try {
            return memcachedClient.withNamespace(region, mc -> mc.decr(key, delta));
        } catch (Exception e) {
            logger.error(e, e);
        }
        return -1;
    }

    public <T> List<T> batchGetEntity(Class<T> clazz, List idList) {
        ArrayList<String> keylist = new ArrayList<>();
        for (Iterator iterator = idList.iterator(); iterator.hasNext(); ) {
            Object id = iterator.next();
            String k = sanitizer.sanitize(id);
            keylist.add(k);
        }
        try {
            Map<String, T> m = memcachedClient.withNamespace(clazz.getTypeName(), mc -> mc.get(keylist));
            ArrayList<T> ret = new ArrayList<>();
            for (int i = 0; i < keylist.size(); i++) {
                String k = keylist.get(i);
                T val = m.get(k);
                ret.add(val);
            }
            if (logger.isDebugEnabled())
                logger.debug("cache batch get:" + clazz.getTypeName() + "," + GsonUtil.toJson(idList));
            return ret;
        } catch (Exception e) {
            logger.error(e, e);
        }
        return null;
    }

    @Override
    public void deleteKey(String region, String key) {
        try {
            if (logger.isDebugEnabled())
                logger.debug("cache del: " + region + "," + key);
            memcachedClient.withNamespace(region, mc -> mc.delete(key));
        } catch (Exception e) {
            logger.error(e, e);
        }
    }

    @Override
    public void setIfNotPresent(String region, String key, Object content, int expire) {

    }
}
