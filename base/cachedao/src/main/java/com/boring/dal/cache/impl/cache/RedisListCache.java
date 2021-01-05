package com.boring.dal.cache.impl.cache;

import com.boring.dal.cache.CacheHelper;
import com.boring.dal.cache.ListCache;
import com.boring.dal.config.Constants;
import com.boring.dal.dao.TxFlusher;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scripting.support.ResourceScriptSource;

import javax.annotation.Resource;
import java.io.ObjectStreamClass;
import java.util.*;

public class RedisListCache implements ListCache {

    public static final Logger logger = LogManager.getLogger("DAO");

    @Autowired(required = false)
    private TxFlusher txFlusher;

    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    @Autowired
    private CacheHelper cacheHelper;

    @Resource
    private DirtyCache dirtyCache;

    private String region;

    private String key;

    private Long optimisticLock;

    private Map<Object, Object> val;

    public RedisListCache(String region, String key) {
        this.region = region;
        this.key = key;
    }

    @Override
    public int inspect() {
        return dirtyCache.inspect(region,key);
    }

    @Override
    public void markDirty() {
        txFlusher.markDirty(region,key);
    }

    @Override
    public void delete() {
        String rkey=cacheHelper.getRegionKey(region,key);
        redisTemplate.delete(rkey);
    }

    @Override
    public void flush() {
        val=null;
    }

    @Override
    public boolean merge(int start, int end, List part) {
        final String rkey=cacheHelper.getRegionKey(region,key);
        if(val==null){
            DefaultRedisScript lua=new DefaultRedisScript();
            lua.setResultType(Long.class);
            lua.setScriptSource(new ResourceScriptSource(new ClassPathResource("/com/boring/dal/cache/impl/lua/set_empty_list.lua")));
            List<String> keys = new ArrayList<>();
            keys.add(rkey);
            keys.add(start+"-"+end);
            return Integer.valueOf(1).equals(redisTemplate.execute(lua, keys, part));
        }else{
            ArrayList<IndexedCache> data=new ArrayList<>();
            for (Iterator<Map.Entry<Object, Object>> iterator = val.entrySet().iterator(); iterator.hasNext(); ) {
                Map.Entry<Object, Object> r1 = iterator.next();
                String[] parts = r1.getKey().toString().split("-");
                IndexedCache ic=new IndexedCache();
                ic.start=Integer.parseInt(parts[0]);
                ic.to=Integer.parseInt(parts[1]);
                ic.data= (List) r1.getValue();
                ic.key=r1.getKey().toString();
            }

            IndexedCache injected = new IndexedCache();
            injected.key=start+"-"+end;
            injected.data=part;
            injected.to=end;
            injected.start=start;
            data.add(injected);

            data.sort(new Comparator<IndexedCache>() {
                @Override
                public int compare(IndexedCache o1, IndexedCache o2) {
                    return o1.start-o2.start;
                }
            });

            ArrayList<String> deleted=new ArrayList<>();
            int idx=data.indexOf(injected);
            if(idx==0){
                idx++;
            }
            IndexedCache merged=data.get(idx-1);

            for (int i = idx; i < data.size(); i++) {
                IndexedCache tmp = mergeIC(merged, data.get(i));
                if(tmp!=null){
                    if(deleted.isEmpty()){
                        deleted.add(merged.key);
                    }
                    deleted.add(data.get(i).key);
                    merged=tmp;
                }
            }
            deleted.remove(injected.key);

            if(deleted.isEmpty()){
                DefaultRedisScript lua=new DefaultRedisScript();
                lua.setResultType(Long.class);
                lua.setScriptSource(new ResourceScriptSource(new ClassPathResource("/com/boring/dal/cache/impl/lua/add_versioned_list.lua")));
                List<String> keys = new ArrayList<>();
                keys.add(rkey);
                keys.add(start+"-"+end);
                return Integer.parseInt(redisTemplate.execute(lua, keys, optimisticLock,part).toString()) > 0;
            }else{
                DefaultRedisScript lua=new DefaultRedisScript();
                lua.setResultType(Long.class);
                lua.setScriptSource(new ResourceScriptSource(new ClassPathResource("/com/boring/dal/cache/impl/lua/set_versioned_list.lua")));
                List<String> keys = new ArrayList<>();
                keys.add(rkey);
                keys.add(merged.start+"-"+merged.to);
                keys.addAll(deleted);
                return Integer.parseInt(redisTemplate.execute(lua, keys, optimisticLock,merged).toString())>0;
            }
        }
    }

    private IndexedCache mergeIC(IndexedCache s1,IndexedCache s2){
        if(s2.start>s1.to){
            return null;
        }
        IndexedCache merged = new IndexedCache();
        merged.data=new ArrayList();
        merged.data.addAll(s1.data);
        merged.data.addAll(s2.data.subList(s2.start-s1.to,s2.data.size()));
        merged.to=s2.to;
        merged.start=s1.start;
        merged.key=merged.start+"-"+merged.to;
        return merged;
    }

    private class IndexedCache{
        int start;
        int to;
        List data;
        String key;
    }

    @Override
    public RangeResult findRange(Integer start, Integer count) {
        get();
        if(val==null)
            return null;
        RangeResult ret=new RangeResult();
        ret.status= Constants.LISTCACHE_MISS;
        Integer toIdx=start+count;
        for (Iterator<Map.Entry<Object, Object>> iterator = val.entrySet().iterator(); iterator.hasNext(); ) {
            Map.Entry<Object, Object> r1 =  iterator.next();
            String[] parts = r1.getKey().toString().split("-");
            int s1=Integer.parseInt(parts[0]);
            int s2=Integer.parseInt(parts[1]);
            if(start>=s1&&toIdx<s2){
                ret.status= Constants.LISTCACHE_HIT;
                ArrayList lst= (ArrayList) r1.getValue();
                ret.actual=lst.subList(s1-start,s1-start+count);
                break;
            }
            if(start>=s1 && start<s2 && toIdx >= s2){
                ArrayList lst= (ArrayList) r1.getValue();
                List tmp = lst.subList(s1 - start, lst.size());
                if(ret.status==Constants.LISTCACHE_MISS||ret.actual.size()<tmp.size()){
                    ret.status=Constants.LISTCACHE_RIGHTHIT;
                    ret.actual=tmp;
                }
                continue;
            }

            if(start<s1&&toIdx>s2&&toIdx<s2){
                ArrayList lst= (ArrayList) r1.getValue();
                List tmp = lst.subList(0,lst.size()-(s2-toIdx));
                if(ret.status==Constants.LISTCACHE_MISS||ret.actual.size()<tmp.size()){
                    ret.status=Constants.LISTCACHE_LEFTHIT;
                    ret.actual=tmp;
                }
                continue;
            }
        }
        return ret;
    }

    private void get(){
        if(val!=null)
            return;

        final String rkey=cacheHelper.getRegionKey(region,key);
        redisTemplate.execute(new SessionCallback<Void>() {
            public Void execute(RedisOperations operations) throws DataAccessException {
                operations.multi();
                optimisticLock= (Long) redisTemplate.opsForValue().get(Constants.VERSIONED+rkey);
                val = redisTemplate.opsForHash().entries(rkey);
                return null;
            }
        });
    }


}
