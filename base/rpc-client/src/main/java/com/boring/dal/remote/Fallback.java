package com.boring.dal.remote;

import com.boring.dal.rpc.common.model.RpcRequest;
import com.boring.dal.rpc.common.model.RpcResponse;

public class Fallback implements RemoteBasicDao {

    @Override
    public RpcResponse getDataListEntity(RpcRequest request) throws Exception {
        return null;
    }

    @Override
    public RpcResponse getDataListMulti(RpcRequest request) throws Exception {
        return null;
    }

    @Override
    public RpcResponse getDataListSingle(RpcRequest request) throws Exception {
        return null;
    }

    @Override
    public RpcResponse countDataList(RpcRequest request) throws Exception {
        return null;
    }

    @Override
    public RpcResponse getDataMapSingle(RpcRequest request) throws Exception {
        return null;
    }

    @Override
    public RpcResponse getDataMapMulti(RpcRequest request) throws Exception {
        return null;
    }

    @Override
    public RpcResponse get(RpcRequest request) throws Exception {
        return null;
    }

    @Override
    public RpcResponse save(RpcRequest request) throws Exception {
        return null;
    }

    @Override
    public RpcResponse update(RpcRequest request) throws Exception {
        return null;
    }

    @Override
    public RpcResponse batchSave(RpcRequest request) throws Exception {
        return null;
    }

    @Override
    public RpcResponse batchGet(RpcRequest request) throws Exception {
        return null;
    }
}
