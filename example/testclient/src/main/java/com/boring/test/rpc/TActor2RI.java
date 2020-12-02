package com.boring.test.rpc;

import java.util.*;
import com.boring.dal.remote.autorpc.AutoRpc;
import com.boring.dal.remote.autorpc.RemoteAccess;
import com.boring.dal.test.model.TActor2;

@AutoRpc
public interface TActor2RI{

    @RemoteAccess
    TActor2 get(java.lang.Integer id_0) throws java.lang.Exception;
    @RemoteAccess
    List<TActor2> batchGet(java.util.List idList_0) throws java.lang.Exception;
    @RemoteAccess
    void update(com.boring.dal.test.model.TActor2 tactor2_0) throws java.lang.Exception;
    @RemoteAccess
    java.lang.Integer save(com.boring.dal.test.model.TActor2 tactor2_0) throws java.lang.Exception;
    @RemoteAccess
    List<java.lang.Integer> batchSave(java.util.List tactor2List_0) throws java.lang.Exception;


}