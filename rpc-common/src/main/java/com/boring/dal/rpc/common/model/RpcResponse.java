package com.boring.dal.rpc.common.model;

import com.alibaba.fastjson.JSON;

import java.util.List;

public class RpcResponse {
    public String result;
    public String type;
    public int isList;

    public RpcResponse(Object result) {
        this.result = JSON.toJSONString(result);

        if (result != null) {
            isList = result instanceof List ? 1 : 0;
            if (isList == 1) {
                List l= (List) result;
                if(l.size()>0)
                    type = l.get(0).getClass().getTypeName();
                else
                    type= Object.class.getTypeName();
            } else {
                type = result.getClass().getTypeName();
            }
        }
    }
    public RpcResponse() {

    }
}
