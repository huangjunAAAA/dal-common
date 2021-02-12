package com.boring.dal.rpc.common.model;

import java.io.Serializable;

public class RpcRequest implements Serializable {
    public String listName;
    public Object[] params;
    public Integer start;
    public Integer count;
    public String clazz;
    public Integer colIdx;
    public Object id;
    public String pojoJsonString;
}
