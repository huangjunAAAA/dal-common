package com.boring.dal.remote.autorpc;

import java.lang.reflect.Proxy;


public class RpcClientFactory {


    private ReflectiveRemote reflectiveRemote;

    public RpcClientFactory(ReflectiveRemote reflectiveRemote) {
        this.reflectiveRemote = reflectiveRemote;
    }

    public <T> T buildClientProxy(Class<T> interfaceType){
        return (T) Proxy.newProxyInstance(interfaceType.getClassLoader(),
                new Class[] {interfaceType},reflectiveRemote);
    }
}
