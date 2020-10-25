package com.rpc.client;

import com.boring.dal.remote.autorpc.AutoRpc;
import com.boring.dal.remote.autorpc.RemoteAccess;
import com.rpc.test.model.TCity;

import java.util.List;

@AutoRpc
public interface TestAutoRpc {
    @RemoteAccess
    TCity get(Object id) throws Exception;
    @RemoteAccess(remote = "getDataListMulti", targetList = "TCity_List1")
    List<Object[]> getTCity_List1(String cityName, Integer start, Integer count) throws Exception;
}
