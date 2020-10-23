package com.boring.dal.test.client;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.boring.dal.remote.RemoteBasicDao;
import com.boring.dal.rpc.common.model.RpcRequest;
import com.boring.dal.rpc.common.model.RpcResponse;
import com.boring.dal.test.model.TCity2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;

@Component
public class TCityDaoWrapper {
    @Autowired
    private RemoteBasicDao remoteBasicDao;

    public TCity2 get(Object id,Object id2,Object id3) throws Exception {
        RpcRequest request = new RpcRequest();
        request.clazz = "com.boring.dal.test.model.TCity2";
        request.id = id;
        RpcResponse res = remoteBasicDao.get(request);
        if (res == null || StringUtils.isEmpty(res.result))
            return null;
        return JSON.parseObject(res.result, TCity2.class);
    }

    public List<TCity2> batchGet(List idlst) throws Exception {
        RpcRequest request = new RpcRequest();
        request.clazz = "com.boring.dal.test.model.TCity2";
        request.params = idlst.toArray(new Object[0]);
        RpcResponse res = remoteBasicDao.batchGet(request);
        if (res == null || StringUtils.isEmpty(res.result))
            return null;
        return JSON.parseObject(res.result, new TypeReference<List<TCity2>>(){});
    }

    public void update(TCity2 city) throws Exception {
        RpcRequest request = new RpcRequest();
        request.clazz="com.boring.dal.test.model.TCity2";
        request.pojoJsonString=JSON.toJSONString(city);
        RpcResponse res = remoteBasicDao.update(request);
    }

    public Integer save(TCity2 city) throws Exception {
        RpcRequest request = new RpcRequest();
        request.clazz="com.boring.dal.test.model.TCity2";
        request.pojoJsonString=JSON.toJSONString(city);
        RpcResponse res = remoteBasicDao.save(request);
        if(res.result==null)
            return null;
        return Integer.parseInt(res.result);
    }

    public List<Integer> batchSave(List citylst) throws Exception {
        RpcRequest request = new RpcRequest();
        request.clazz="com.boring.dal.test.model.TCity2";
        request.pojoJsonString=JSON.toJSONString(citylst);
        RpcResponse res = remoteBasicDao.batchSave(request);
        if(res.result==null)
            return null;
        return JSON.parseObject(res.result, new TypeReference<List<Integer>>(){});
    }



    public List<Object[]> getTCountryCity_List1(String countryName,String cityName, Integer start, Integer count) throws Exception {
        RpcRequest request = new RpcRequest();
        request.listName = "TCountryCity_List1";
        request.start = start;
        request.count= count;
        request.params=new Object[]{cityName,countryName};
        RpcResponse res = remoteBasicDao.getDataListMulti(request);
        if (res == null || StringUtils.isEmpty(res.result))
            return null;
        return JSON.parseObject(res.result, new TypeReference<List<Object[]>>(){});
    }

    public List<String> getTCountryCity_List1Forcity_id(String countryName,String cityName, Integer start, Integer count) throws Exception {
        RpcRequest request = new RpcRequest();
        request.listName = "TCountryCity_List1";
        request.start = start;
        request.count= count;
        request.colIdx=0;
        request.params=new Object[]{cityName,countryName};
        RpcResponse res = remoteBasicDao.getDataListSingle(request);
        if (res == null || StringUtils.isEmpty(res.result))
            return null;
        return JSON.parseObject(res.result, new TypeReference<List<String>>(){});
    }

    public List<Object[]> getTCountryCity_List2(Integer countryId, Integer start, Integer count) throws Exception {
        RpcRequest request = new RpcRequest();
        request.listName = "TCountryCity_List2";
        request.start = start;
        request.count= count;
        request.params=new Object[]{countryId};
        RpcResponse res = remoteBasicDao.getDataListMulti(request);
        if (res == null || StringUtils.isEmpty(res.result))
            return null;
        return JSON.parseObject(res.result, new TypeReference<List<Object[]>>(){});
    }

    public List<String> getTCountryCity_List2Forcity(Integer countryId, Integer start, Integer count) throws Exception {
        RpcRequest request = new RpcRequest();
        request.listName = "TCountryCity_List2";
        request.start = start;
        request.count= count;
        request.colIdx=0;
        request.params=new Object[]{countryId};
        RpcResponse res = remoteBasicDao.getDataListSingle(request);
        if (res == null || StringUtils.isEmpty(res.result))
            return null;
        return JSON.parseObject(res.result, new TypeReference<List<String>>(){});
    }

    public <T> List<T> getTCountryCity_List1Forcity_idEntity(String countryName,String cityName,Class<T> clazz, Integer start, Integer count) throws Exception {
        RpcRequest request = new RpcRequest();
        request.listName = "TCountryCity_List1";
        request.start = start;
        request.count= count;
        request.colIdx= 0;
        request.clazz=clazz.getTypeName();
        request.params=new Object[]{cityName,countryName};
        RpcResponse res = remoteBasicDao.getDataListEntity(request);
        if (res == null || StringUtils.isEmpty(res.result))
            return null;
        return JSON.parseObject(res.result, new TypeReference<List<T>>(){});
    }

    public Integer countTCountryCity_List1(String countryName,String cityName) throws Exception {
        RpcRequest request = new RpcRequest();
        request.listName = "TCountryCity_List1";
        request.params=new Object[]{cityName,countryName};
        RpcResponse res = remoteBasicDao.countDataList(request);
        if(res.result==null)
            return null;
        return Integer.parseInt(res.result);
    }

    public Integer getTCountryCity_Map1Forcity_id(Integer cityId) throws Exception {
        RpcRequest request = new RpcRequest();
        request.listName = "TCountryCity_map1";
        request.colIdx=0;
        request.params=new Object[]{cityId};
        RpcResponse res = remoteBasicDao.getDataMapSingle(request);
        if (res == null || StringUtils.isEmpty(res.result))
            return null;
        return JSON.parseObject(res.result, Integer.class);
    }

    public Object[] getTCountryCity_Map1(Integer cityId) throws Exception {
        RpcRequest request = new RpcRequest();
        request.listName = "TCountryCity_map1";
        request.params=new Object[]{cityId};
        RpcResponse res = remoteBasicDao.getDataMapMulti(request);
        if (res == null || StringUtils.isEmpty(res.result))
            return null;
        return JSON.parseObject(res.result, new Object[0].getClass());
    }

    public String getTCountryCity_Map2Forcity(Integer cityId) throws Exception {
        RpcRequest request = new RpcRequest();
        request.listName = "TCountryCity_map2";
        request.colIdx=0;
        request.params=new Object[]{cityId};
        RpcResponse res = remoteBasicDao.getDataMapSingle(request);
        if (res == null || StringUtils.isEmpty(res.result))
            return null;
        return JSON.parseObject(res.result, String.class);
    }

    public Object[] getTCountryCity_Map2(Integer cityId) throws Exception {
        RpcRequest request = new RpcRequest();
        request.listName = "TCountryCity_map2";
        request.params=new Object[]{cityId};
        RpcResponse res = remoteBasicDao.getDataMapMulti(request);
        if (res == null || StringUtils.isEmpty(res.result))
            return null;
        return JSON.parseObject(res.result, new Object[0].getClass());
    }

}
