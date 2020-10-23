package com.boring.dal.remote;

import com.boring.dal.rpc.common.model.RpcRequest;
import com.boring.dal.rpc.common.model.RpcResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient(name = "dao-server", fallback = Fallback.class,configuration = {RpcFeignCfg.class})
@RequestMapping("/remoteDao")
public interface RemoteBasicDao {
    @RequestMapping("/getDataListEntity")
    RpcResponse getDataListEntity(@RequestBody RpcRequest request) throws Exception;

    @RequestMapping("/getDataListMulti")
    RpcResponse getDataListMulti(@RequestBody RpcRequest request) throws Exception;

    @RequestMapping("/getDataListSingle")
    RpcResponse getDataListSingle(@RequestBody RpcRequest request) throws Exception;

    @RequestMapping("/countDataList")
    RpcResponse countDataList(@RequestBody RpcRequest request) throws Exception;

    @RequestMapping("/getDataMapSingle")
    RpcResponse getDataMapSingle(@RequestBody RpcRequest request) throws Exception;

    @RequestMapping("/getDataMapMulti")
    RpcResponse getDataMapMulti(@RequestBody RpcRequest request) throws Exception;

    @RequestMapping("/get")
    RpcResponse get(@RequestBody RpcRequest request) throws Exception;

    @RequestMapping("/save")
    RpcResponse save(@RequestBody RpcRequest request) throws Exception;

    @RequestMapping("/update")
    RpcResponse update(@RequestBody RpcRequest request) throws Exception;

    @RequestMapping("/batchSave")
    RpcResponse batchSave(@RequestBody RpcRequest request) throws Exception;

    @RequestMapping("/batchGet")
    RpcResponse batchGet(@RequestBody RpcRequest request) throws Exception;
}
