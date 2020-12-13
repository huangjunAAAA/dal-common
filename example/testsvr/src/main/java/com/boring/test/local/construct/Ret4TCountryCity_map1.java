package com.boring.test.local.construct;
import java.util.*;

public class Ret4TCountryCity_map1{

    public Integer city_city_id;
    public String city;
    public static Ret4TCountryCity_map1 fromObjectArray(Object[] oay){
        Ret4TCountryCity_map1 ret = new Ret4TCountryCity_map1();
        ret.city_city_id = (Integer)oay[0];
        ret.city = (String)oay[1];
        return ret;
    }

    public static List<Ret4TCountryCity_map1> fromObjectArrayList(List<Object[]> oaylist){
        List<Ret4TCountryCity_map1> rlst=new ArrayList<>();
        for(int i=0; i<oaylist.size(); ++i){
            Ret4TCountryCity_map1 ro= fromObjectArray(oaylist.get(i));
            rlst.add(ro);
        }
        return rlst;
    }
}