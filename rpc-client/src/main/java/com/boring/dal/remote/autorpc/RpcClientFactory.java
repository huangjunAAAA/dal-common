package com.boring.dal.remote.autorpc;

import java.lang.reflect.Proxy;


public class RpcClientFactory {


    private BasicDaoProxy basicDaoProxy;

    public RpcClientFactory(BasicDaoProxy basicDaoProxy) {
        this.basicDaoProxy = basicDaoProxy;
    }

    public <T> T buildClientProxy(Class<T> interfaceType){
        return (T) Proxy.newProxyInstance(interfaceType.getClassLoader(),
                new Class[] {interfaceType},basicDaoProxy);
    }
}
