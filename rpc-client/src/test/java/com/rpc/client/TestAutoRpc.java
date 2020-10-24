package com.rpc.client;

import com.boring.dal.remote.autorpc.AutoRpc;
import com.rpc.test.model.TCity;

import java.util.List;

@AutoRpc
public interface TestAutoRpc {
    TCity get(Object id) throws Exception;
    List<Object[]> getTCity_List1(String countryName, String cityName, Integer start, Integer count) throws Exception;
}
