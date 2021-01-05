package com.boring.dal.cache.impl.cache;

import com.boring.dal.cache.BatchEntityCache;
import com.boring.dal.cache.EntityCache;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

@Component
public class EntityCacheFactory {
    @Autowired
    private AutowireCapableBeanFactory autowireCapableBeanFactory;
    public <T> EntityCache<T> createEntityCache(String region,String key, boolean fill){
        RedisEntityCache ec=new RedisEntityCache(region,key);
        if(fill)
            ec.get();
        return ec;
    }


    public <T> BatchEntityCache<T> createBatchCache(String region, List<String> keys, boolean fill){
        SimpleBEC sbec=new SimpleBEC();
        for (Iterator<String> iterator = keys.iterator(); iterator.hasNext(); ) {
            String k =  iterator.next();
            EntityCache<Object> cache = createEntityCache(region, k, fill);
            sbec.val.put(k,cache);
        }
        return sbec;
    }

    public class SimpleBEC implements BatchEntityCache{

        LinkedHashMap<String,EntityCache> val=new LinkedHashMap<>();

        @Override
        public List<EntityCache> get() {
            return null;
        }

        @Override
        public EntityCache getByKey(String key) {
            return null;
        }
    }

}
