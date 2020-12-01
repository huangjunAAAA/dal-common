package com.boring.dal.cache;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

@Configuration
public class CacheConf {

//    @Value("${dao.memcache}")
//    private String mcconfig;
//
//    @Bean("memcachedConfig")
//    public MemCacheConfigFile loadMemcache() {
//        return YamlFileLoader.loadConfigFromPath(mcconfig, MemCacheConfigFile.class);
//    }


//    @Bean("memcachedClient")
//    public MemcachedClient buildMC() throws Exception {
//        MemCacheConfigFile memCacheConfigFile = loadMemcache();
//        StringBuilder alst = new StringBuilder();
//        for (Iterator<MemSvrConfig> iterator = memCacheConfigFile.getMemcache().iterator(); iterator.hasNext(); ) {
//            MemSvrConfig n = iterator.next();
//            alst.append(n.getAddr()).append(":").append(n.getPort());
//            if (iterator.hasNext()) {
//                alst.append(" ");
//            }
//        }
//        MemcachedClientBuilder builder = new XMemcachedClientBuilder(
//                AddrUtil.getAddresses(alst.toString()));
//        builder.setTranscoder(new SerializingTranscoder());
//        builder.setSanitizeKeys(false);
//        builder.setSessionLocator(new ElectionMemcachedSessionLocator());
//        MemcachedClient mc = builder.build();
//        return mc;
//    }

    @Bean(name = "cacheRedis")
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory){
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);
        return template;
    }
}
