package com.boring.dal.cache.impl;

import com.boring.dal.cache.SimpleCache;
import com.boring.dal.cache.sanitizer.KeySanitizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

@Component
public class RedisCache implements SimpleCache {

    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    @Autowired
    private KeySanitizer keySanitizer;

    private static final String delimiter=":";

    @Override
    public <T> T get(String region, String key, Supplier<T> supplier) {
        String rkey=getRegionKey(region,key);
        T t= (T) redisTemplate.opsForValue().get(rkey);
        return t;
    }

    @Override
    public <T> T get(String region, String key, Class<T> tClass) {
        String rkey=getRegionKey(region,key);
        T t= (T) redisTemplate.opsForValue().get(rkey);
        return t;
    }

    @Override
    public <T> T getEntity(String key, Class<T> tClass) {
        return get(tClass.getTypeName(),key,tClass);
    }

    @Override
    public void setEntity(String key, Object obj) {
        setEntity(key,obj,0);
    }

    @Override
    public void setEntity(String key, Object obj, int expire) {
        setRaw(obj.getClass().getTypeName(),key,obj,expire);
    }

    @Override
    public void setRaw(String region, String key, Object content, int expire) {
        String rkey=getRegionKey(region,key);
        if(expire>0)
            redisTemplate.opsForValue().set(rkey,content,expire, TimeUnit.SECONDS);
        else
            redisTemplate.opsForValue().set(rkey,content);
    }

    @Override
    public long incr(String region, String key, long delta) {
        String rkey=getRegionKey(region,key);
        return redisTemplate.opsForValue().increment(rkey,delta);
    }

    @Override
    public long decr(String region, String key, long delta) {
        String rkey=getRegionKey(region,key);
        return redisTemplate.opsForValue().increment(rkey,-delta);
    }

    @Override
    public <T> List<T> batchGetEntity(Class<T> clazz, List idList) {
        ArrayList<String> rkeylst=new ArrayList<>();
        idList.stream().forEach(s->rkeylst.add(getRegionKey(clazz.getTypeName(),s.toString())));
        List lst = redisTemplate.opsForValue().multiGet(rkeylst);
        return lst;
    }

    @Override
    public void deleteKey(String region, String key) {
        String rkey=getRegionKey(region,key);
        redisTemplate.delete(rkey);
    }

    @Override
    public void setIfNotPresent(String region, String key, Object content, int expire) {
        String rkey=getRegionKey(region,key);
        if(expire>0)
            redisTemplate.opsForValue().setIfAbsent(rkey,content);
        else
            redisTemplate.opsForValue().setIfAbsent(rkey,content);
    }

    private String getRegionKey(String region,String key){
        return region.replaceAll("\\.",delimiter)+delimiter+keySanitizer.sanitize(key);
    }
}
