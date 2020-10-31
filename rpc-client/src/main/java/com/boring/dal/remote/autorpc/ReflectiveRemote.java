package com.boring.dal.remote.autorpc;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.boring.dal.remote.RemoteBasicDao;
import com.boring.dal.rpc.common.model.RpcRequest;
import com.boring.dal.rpc.common.model.RpcResponse;
import org.springframework.util.StringUtils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class ReflectiveRemote implements InvocationHandler {

    private RemoteBasicDao remoteBasicDao;

    public ReflectiveRemote(RemoteBasicDao remoteBasicDao) {
        this.remoteBasicDao=remoteBasicDao;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        RemoteAccess ra = method.getAnnotation(RemoteAccess.class);
        if(ra==null)
            throw new RuntimeException("missing Annotation @RemoteAccess");
        String rname = ra.remote();
        if(StringUtils.isEmpty(rname))
            rname=method.getName();
        String listname=ra.targetList();
        int idx=ra.idx();
        switch (rname){
            case "get":{
                return get(args[0],method.getReturnType());
            }
            case "batchGet": {
                ParameterizedType rt = (ParameterizedType) method.getAnnotatedReturnType().getType();
                Class<?> clazz = Class.forName(rt.getActualTypeArguments()[0].getTypeName());
                return batchGet((List) args[0], clazz);
            }
            case "update": {
                update(args[0]);
                return null;
            }
            case "save":{
                return save(args[0]);
            }
            case "batchSave":{
                return batchSave((List) args[0]);
            }
            case "getDataListMulti": {
                Object[] actualargs = Arrays.copyOfRange(args, 0, args.length - 2);
                return getDataListMulti(listname, actualargs, (Integer) args[args.length - 2], (Integer) args[args.length - 1]);
            }
            case "getDataListSingle": {
                ParameterizedType rt = (ParameterizedType) method.getAnnotatedReturnType().getType();
                Class<?> clazz = Class.forName(rt.getActualTypeArguments()[0].getTypeName());
                Object[] actualargs = Arrays.copyOfRange(args, 0, args.length - 2);
                return getDataListSingle(listname, actualargs, idx,(Integer) args[args.length - 2], (Integer) args[args.length - 1],clazz);
            }
            case "getDataListEntity": {
                Object[] actualargs = Arrays.copyOfRange(args, 0, args.length - 3);
                return getDataListEntity(listname, actualargs,idx,(Integer) args[args.length - 3], (Integer) args[args.length - 2], (Class) args[args.length - 1]);
            }
            case "countDataList": {
                return countDataList(listname, args);
            }
            case "getDataMapSingle": {
                return getDataMapSingle(listname, args,idx);
            }
            case "getDataMapMulti": {
                return getDataMapMulti(listname, args);
            }
        }
        throw new RuntimeException("unknown method:["+rname+"]");
    }

    public Object get(Object id, Class<?> clazz) throws Exception {
        RpcRequest request = new RpcRequest();
        request.clazz = clazz.getTypeName();
        request.id = id;
        RpcResponse res = remoteBasicDao.get(request);
        if (res == null || StringUtils.isEmpty(res.result))
            return null;
        return JSON.parseObject(res.result, clazz);
    }

    public List batchGet(List idlst, Class<?> clazz) throws Exception {
        RpcRequest request = new RpcRequest();
        request.clazz = clazz.getTypeName();
        request.params = idlst.toArray(new Object[0]);
        RpcResponse res = remoteBasicDao.batchGet(request);
        if (res == null || StringUtils.isEmpty(res.result))
            return null;
        return JSON.parseArray(res.result,clazz);
    }

    public void update(Object obj) throws Exception {
        RpcRequest request = new RpcRequest();
        request.clazz=obj.getClass().getTypeName();
        request.pojoJsonString=JSON.toJSONString(obj);
        RpcResponse res = remoteBasicDao.update(request);
    }

    public Integer save(Object obj) throws Exception {
        RpcRequest request = new RpcRequest();
        request.clazz=obj.getClass().getTypeName();
        request.pojoJsonString=JSON.toJSONString(obj);
        RpcResponse res = remoteBasicDao.save(request);
        if(res.result==null)
            return null;
        return Integer.parseInt(res.result);
    }

    public List<Integer> batchSave(List objlst) throws Exception {
        if(objlst==null||objlst.size()==0)
            return new ArrayList<>();
        RpcRequest request = new RpcRequest();
        request.clazz=objlst.get(0).getClass().getTypeName();
        request.pojoJsonString=JSON.toJSONString(objlst);
        RpcResponse res = remoteBasicDao.batchSave(request);
        if(res.result==null)
            return null;
        return JSON.parseArray(res.result, Integer.class);
    }



    public List<Object[]> getDataListMulti(String listname, Object[] args, Integer start, Integer count) throws Exception {
        RpcRequest request = new RpcRequest();
        request.listName = listname;
        request.start = start;
        request.count= count;
        request.params=args;
        RpcResponse res = remoteBasicDao.getDataListMulti(request);
        if (res == null || StringUtils.isEmpty(res.result))
            return null;
        return JSON.parseObject(res.result, new TypeReference<List<Object[]>>(){});
    }

    public List getDataListSingle(String listname, Object[] args, Integer idx, Integer start, Integer count,Class<?> clazz) throws Exception {
        RpcRequest request = new RpcRequest();
        request.listName = listname;
        request.start = start;
        request.count= count;
        request.colIdx=idx;
        request.params=args;
        RpcResponse res = remoteBasicDao.getDataListSingle(request);
        if (res == null || StringUtils.isEmpty(res.result))
            return null;
        return JSON.parseArray(res.result, clazz);
    }


    public List getDataListEntity(String listname, Object[] args , Integer idx, Integer start, Integer count,Class clazz) throws Exception {
        RpcRequest request = new RpcRequest();
        request.listName = listname;
        request.start = start;
        request.count= count;
        request.colIdx= idx;
        request.clazz=clazz.getTypeName();
        request.params=args;
        RpcResponse res = remoteBasicDao.getDataListEntity(request);
        if (res == null || StringUtils.isEmpty(res.result))
            return null;
        return JSON.parseArray(res.result, clazz);
    }

    public Integer countDataList(String listname, Object[] args) throws Exception {
        RpcRequest request = new RpcRequest();
        request.listName = listname;
        request.params=args;
        RpcResponse res = remoteBasicDao.countDataList(request);
        if(res.result==null)
            return null;
        return Integer.parseInt(res.result);
    }

    public Object getDataMapSingle(String listname, Object[] args, Integer idx) throws Exception {
        RpcRequest request = new RpcRequest();
        request.listName = listname;
        request.colIdx=idx;
        request.params=args;
        RpcResponse res = remoteBasicDao.getDataMapSingle(request);
        if (res == null || StringUtils.isEmpty(res.result))
            return null;
        return JSON.parseObject(res.result, Class.forName(res.type));
    }

    public Object[] getDataMapMulti(String listname, Object[] args) throws Exception {
        RpcRequest request = new RpcRequest();
        request.listName = listname;
        request.params=args;
        RpcResponse res = remoteBasicDao.getDataMapMulti(request);
        if (res == null || StringUtils.isEmpty(res.result))
            return null;
        return JSON.parseObject(res.result, new Object[0].getClass());
    }
}
