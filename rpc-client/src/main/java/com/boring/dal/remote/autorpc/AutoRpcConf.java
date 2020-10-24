package com.boring.dal.remote.autorpc;

import com.boring.dal.remote.RemoteBasicDao;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AutoRpcConf {
    @Bean(name = "remoteBasicDao")
    public BasicDaoProxy remoteBasicDao(RemoteBasicDao remoteBasicDao){
        return new BasicDaoProxy(remoteBasicDao);
    }

    @Bean(name = "rpcClientFactory")
    public RpcClientFactory rpcClientFactory(BasicDaoProxy basicDaoProxy){
        return new RpcClientFactory(basicDaoProxy);
    }
}
