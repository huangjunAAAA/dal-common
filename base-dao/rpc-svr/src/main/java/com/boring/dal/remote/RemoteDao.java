package com.boring.dal.remote;

import com.alibaba.fastjson.JSON;
import com.boring.dal.cache.ComprehensiveDao;
import com.boring.dal.rpc.common.model.RpcRequest;
import com.boring.dal.rpc.common.model.RpcResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;


@RestController
@RequestMapping("/remoteDao")
@EnableDiscoveryClient
public class RemoteDao implements RemoteBasicDao {
    private static final Logger logger = LogManager.getLogger("DAO");

    @Resource
    private ComprehensiveDao comprehensiveDao;


    @RequestMapping("/getDataListEntity")
    public RpcResponse getDataListEntity(@RequestBody RpcRequest request) throws Exception {
        if (logger.isDebugEnabled())
            logger.debug("rpc getDataListEntity:" + JSON.toJSONString(request));
        List ret = comprehensiveDao.getDataListEntity(request.listName, request.params, request.start, request.count, request.clazz, request.colIdx);
        RpcResponse res = new RpcResponse(ret);
        return res;
    }


    @RequestMapping("/getDataListMulti")
    public RpcResponse getDataListMulti(@RequestBody RpcRequest request) throws Exception {
        if (logger.isDebugEnabled())
            logger.debug("rpc getDataListMulti:" + JSON.toJSONString(request));
        List<Object[]> ret = comprehensiveDao.getDataListMulti(request.listName, request.params, request.start, request.count);
        RpcResponse res = new RpcResponse(ret);
        return res;
    }


    @RequestMapping("/getDataListSingle")
    public RpcResponse getDataListSingle(@RequestBody RpcRequest request) throws Exception {
        if (logger.isDebugEnabled())
            logger.debug("rpc getDataListSingle:" + JSON.toJSONString(request));
        List<Object> ret = comprehensiveDao.getDataListSingle(request.listName, request.params, request.start, request.count, request.colIdx);
        RpcResponse res = new RpcResponse(ret);
        return res;
    }


    @RequestMapping("/countDataList")
    public RpcResponse countDataList(@RequestBody RpcRequest request) throws Exception {
        if (logger.isDebugEnabled())
            logger.debug("rpc countDataList:" + JSON.toJSONString(request));
        Integer ret = comprehensiveDao.countDataList(request.listName, request.params);
        RpcResponse res = new RpcResponse();
        if(ret!=null)
            res.result=ret.toString();
        return res;
    }


    @RequestMapping("/getDataMapSingle")
    public RpcResponse getDataMapSingle(@RequestBody RpcRequest request) throws Exception {
        if (logger.isDebugEnabled())
            logger.debug("rpc getDataMapSingle:" + JSON.toJSONString(request));
        Object ret = comprehensiveDao.getDataMapSingle(request.listName, request.params, request.colIdx);
        RpcResponse res = new RpcResponse(ret);
        return res;
    }


    @RequestMapping("/getDataMapMulti")
    public RpcResponse getDataMapMulti(@RequestBody RpcRequest request) throws Exception {
        if (logger.isDebugEnabled())
            logger.debug("rpc getDataMapMulti:" + JSON.toJSONString(request));
        Object[] ret = comprehensiveDao.getDataMapMulti(request.listName, request.params);
        RpcResponse res = new RpcResponse(ret);
        return res;
    }

    @RequestMapping("/get")
    public RpcResponse get(@RequestBody RpcRequest request) throws Exception {
        if (logger.isDebugEnabled())
            logger.debug("rpc get:" + JSON.toJSONString(request));
        Object ret = comprehensiveDao.get(request.id, request.clazz);
        RpcResponse res = new RpcResponse(ret);
        return res;
    }


    @RequestMapping("/save")
    public RpcResponse save(@RequestBody RpcRequest request) throws Exception {
        if (logger.isDebugEnabled())
            logger.debug("rpc save:" + JSON.toJSONString(request));
        Class<?> cls = Class.forName(request.clazz);
        Object o = JSON.parseObject(request.pojoJsonString, cls);
        Object id = comprehensiveDao.save(o);
        RpcResponse res = new RpcResponse();
        if(id!=null)
            res.result=id.toString();
        return res;
    }


    @RequestMapping("/update")
    public RpcResponse update(@RequestBody RpcRequest request) throws Exception {
        if (logger.isDebugEnabled())
            logger.debug("rpc update:" + JSON.toJSONString(request));
        Class<?> cls = Class.forName(request.clazz);
        Object o = JSON.parseObject(request.pojoJsonString, cls);
        comprehensiveDao.update(o);
        return new RpcResponse(null);
    }


    @RequestMapping("/batchSave")
    public RpcResponse batchSave(@RequestBody RpcRequest request) throws Exception {
        if (logger.isDebugEnabled())
            logger.debug("rpc batchSave:" + JSON.toJSONString(request));
        Class<?> cls = Class.forName(request.clazz);
        List<?> lst = JSON.parseArray(request.pojoJsonString, cls);
        List ret = comprehensiveDao.batchSave(lst);
        RpcResponse res = new RpcResponse(ret);
        return res;
    }


    @RequestMapping("/batchGet")
    public RpcResponse batchGet(@RequestBody RpcRequest request) throws Exception {
        if (logger.isDebugEnabled())
            logger.debug("rpc batchGet:" + JSON.toJSONString(request));
        List<Object> ret = comprehensiveDao.batchGet(Arrays.asList(request.params), request.clazz);
        RpcResponse res = new RpcResponse(ret);
        return res;
    }
}
