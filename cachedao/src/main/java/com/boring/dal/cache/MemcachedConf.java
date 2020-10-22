package com.boring.dal.cache;

import com.boring.dal.YamlFileLoader;
import com.boring.dal.cache.config.MemCacheConfigFile;
import com.boring.dal.cache.config.MemSvrConfig;
import net.rubyeye.xmemcached.MemcachedClient;
import net.rubyeye.xmemcached.MemcachedClientBuilder;
import net.rubyeye.xmemcached.XMemcachedClientBuilder;
import net.rubyeye.xmemcached.impl.ElectionMemcachedSessionLocator;
import net.rubyeye.xmemcached.transcoders.SerializingTranscoder;
import net.rubyeye.xmemcached.utils.AddrUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Iterator;

@Configuration
public class MemcachedConf {

    @Value("${dao.memcache}")
    private String mcconfig;

    @Bean("memcachedConfig")
    public MemCacheConfigFile loadMemcache() {
        return YamlFileLoader.loadConfigFromPath(mcconfig, MemCacheConfigFile.class);
    }

    @Bean("memcachedClient")
    public MemcachedClient buildMC() throws Exception {
        MemCacheConfigFile memCacheConfigFile = loadMemcache();
        StringBuilder alst = new StringBuilder();
        for (Iterator<MemSvrConfig> iterator = memCacheConfigFile.getMemcache().iterator(); iterator.hasNext(); ) {
            MemSvrConfig n = iterator.next();
            alst.append(n.getAddr()).append(":").append(n.getPort());
            if (iterator.hasNext()) {
                alst.append(" ");
            }
        }
        MemcachedClientBuilder builder = new XMemcachedClientBuilder(
                AddrUtil.getAddresses(alst.toString()));
        builder.setTranscoder(new SerializingTranscoder());
        builder.setSanitizeKeys(false);
        builder.setSessionLocator(new ElectionMemcachedSessionLocator());
        MemcachedClient mc = builder.build();
        return mc;
    }
}
