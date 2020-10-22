package com.rpc.client;

import com.alibaba.fastjson.JSON;
import com.boring.dal.remote.RemoteBasicDao;
import com.boring.dal.rpc.common.model.RpcRequest;
import com.boring.dal.rpc.common.model.RpcResponse;
import com.rpc.test.model.TCity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class TCityDaoWrapper {
    @Autowired
    private RemoteBasicDao remoteBasicDao;

    public TCity get(Object id) throws Exception {
        RpcRequest request = new RpcRequest();
        request.clazz = TCity.class.getTypeName();
        request.id = id;
        RpcResponse res = remoteBasicDao.get(request);
        if (res == null || StringUtils.isEmpty(res.result))
            return null;
        return JSON.parseObject(res.result, TCity.class);
    }
}
