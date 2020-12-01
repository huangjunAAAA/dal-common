package com.boring.dal.cache.config;

import java.util.List;

public class MemCacheConfigFile {
    private List<MemSvrConfig> memcache;

    public List<MemSvrConfig> getMemcache() {
        return memcache;
    }

    public void setMemcache(List<MemSvrConfig> memcache) {
        this.memcache = memcache;
    }
}
