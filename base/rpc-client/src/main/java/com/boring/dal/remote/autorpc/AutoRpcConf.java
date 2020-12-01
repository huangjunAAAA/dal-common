package com.boring.dal.remote.autorpc;

import com.boring.dal.remote.RemoteBasicDao;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AutoRpcConf {
    @Bean
    public ReflectiveRemote remoteBasicDao(RemoteBasicDao remoteBasicDao){
        return new ReflectiveRemote(remoteBasicDao);
    }

    @Bean(name = "rpcClientFactory")
    public RpcClientFactory rpcClientFactory(ReflectiveRemote reflectiveRemote){
        return new RpcClientFactory(reflectiveRemote);
    }
}
