package com.boring.dal.test.client;


import com.boring.dal.test.client.rpc.TCityD2;
import com.boring.dal.test.model.TCity2;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.boring")
@ComponentScan(basePackages = {"com.boring"})
@RestController
public class TheClient {

    @Autowired
    private TCityD2 td2;

    public static void main(String[] args) throws Exception {
        ConfigurableApplicationContext context = SpringApplication.run(TheClient.class, args);
    }

    @GetMapping("/testclient")
    public String test() throws Exception {
//        List<Object[]> cc = tCityDaoWrapper.getTCountryCity_List1("China", "client111", 0, 4);
        TCity2 city = td2.get(47);
        city.setCity("client111");
        td2.update(city);
        List<Object[]> cc2 = td2.getTCountryCity_List1("India", "client111", 0, 4);
        return "testok:" + city;
    }

    @GetMapping("/testclient2")
    public String test2() throws Exception {
        List<Object[]> cc = td2.getTCountryCity_List1("China", "client222", 0, 4);
        TCity2 city = td2.get(47);
        city.setCity("client222");
        td2.update(city);
        List<Object[]> cc2 = td2.getTCountryCity_List1("India", "client222", 0, 4);
        return "testok:" + city;
    }

    @GetMapping("/test4")
    public String test4() throws Exception {
        StringBuilder ret = new StringBuilder();
        Object[] map11 = td2.getTCountryCity_Map2(16);
        ret.append("get map2 multi:"+ Arrays.toString(map11)).append("<br>");

        String maplst21 = td2.getTCountryCity_Map2Forcity(16);
        ret.append("get map2 single:"+maplst21).append("<br>");
        return ret.toString();
    }


    @GetMapping("/testclientrest")
    public String test3() throws Exception {
        StringBuilder ret = new StringBuilder();
        TCity2 c1 = new TCity2();
        c1.setCity("testcity1");
        c1.setCountryId(44);
        Integer c1id = td2.save(c1);
        ret.append("save:"+c1id).append("<br>");
        TCity2 c2 = new TCity2();
        c2.setCity("testcity2");
        c2.setCountryId(44);
        ArrayList<TCity2> some=new ArrayList<>();
        some.add(c1);
        some.add(c2);
        List<Integer> idls = td2.batchSave(some);
        ret.append("batch save:"+new Gson().toJson(idls)).append("<br>");

        idls.clear();
        idls.add(5);
        idls.add(7);
        List<TCity2> citys = td2.batchGet(idls);
        ret.append("batch get:"+new Gson().toJson(citys)).append("<br>");

        Integer c = td2.countTCountryCity_List1("China", "Binzhou");
        ret.append("count:"+c).append("<br>");

        TCity2 cc = td2.get(89);
        ret.append("get:89"+new Gson().toJson(cc)).append("<br>");

        List<Object[]> tlst = td2.getTCountryCity_List1("China", "Binzhou", 0, 4);
        List<Object[]> flst = td2.getTCountryCity_List1("China", "Binzhou", 3, 2);
        ret.append("get list1 multi:"+new Gson().toJson(tlst)).append("<br>");
        ret.append("false get list1 multi:"+new Gson().toJson(flst)).append("<br>");

        List<String> cityidl = td2.getTCountryCity_List1Forcity_id("China", "Binzhou", 0, 4);
        ret.append("get list1 single:"+new Gson().toJson(cityidl)).append("<br>");

        List<Object[]> tlst2 = td2.getTCountryCity_List2(44, 0, 4);
        ret.append("get list2 multi:"+new Gson().toJson(tlst2)).append("<br>");

        List<String> cityidl2 = td2.getTCountryCity_List2Forcity(44, 0, 4);
        ret.append("get list2 single:"+new Gson().toJson(cityidl2)).append("<br>");


        List<TCity2> centity = td2.getTCountryCity_List1Forcity_idEntity("India", "Adoni", 0, 4, TCity2.class);
        ret.append("get list entity:"+new Gson().toJson(centity)).append("<br>");

        Object[] map1 = td2.getTCountryCity_Map1(16);
        ret.append("get map multi:"+ Arrays.toString(map1)).append("<br>");

        Integer maplst2 = td2.getTCountryCity_Map1Forcity_id(16);
        ret.append("get map single:"+maplst2).append("<br>");

        Object[] map11 = td2.getTCountryCity_Map2(16);
        ret.append("get map2 multi:"+ Arrays.toString(map11)).append("<br>");

        String maplst21 = td2.getTCountryCity_Map2Forcity(16);
        ret.append("get map2 single:"+maplst21).append("<br>");

        return ret.toString();
    }
}
