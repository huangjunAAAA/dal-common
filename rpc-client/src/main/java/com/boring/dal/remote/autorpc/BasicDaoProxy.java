package com.boring.dal.remote.autorpc;

import com.alibaba.fastjson.JSON;
import com.boring.dal.remote.RemoteBasicDao;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;


public class BasicDaoProxy implements InvocationHandler {

    private RemoteBasicDao remoteBasicDao;

    public BasicDaoProxy(RemoteBasicDao remoteBasicDao) {
        this.remoteBasicDao=remoteBasicDao;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (Object.class.equals(method.getDeclaringClass())) {
            return method.invoke(this,args);
        }
        System.out.println(remoteBasicDao+"--调用前，参数：{}" + args);
        //这里可以得到参数数组和方法等，可以通过反射，注解等，进行结果集的处理
        //mybatis就是在这里获取参数和相关注解，然后根据返回值类型，进行结果集的转换
        Object result = JSON.toJSONString(args);
        System.out.println(remoteBasicDao+"-"+"-调用后，结果：{}" + result);
        return method.getReturnType().newInstance();
    }
}
