package com.boring.test.local.construct;
import java.util.*;

public class Ret4TCountryCity_List1{

    public Integer city_city_id;
    public Integer country_country_id;
    public static Ret4TCountryCity_List1 fromObjectArray(Object[] oay){
        Ret4TCountryCity_List1 ret = new Ret4TCountryCity_List1();
        ret.city_city_id = (Integer)oay[0];
        ret.country_country_id = (Integer)oay[1];
        return ret;
    }

    public static List<Ret4TCountryCity_List1> fromObjectArrayList(List<Object[]> oaylist){
        List<Ret4TCountryCity_List1> rlst=new ArrayList<>();
        for(int i=0; i<oaylist.size(); ++i){
            Ret4TCountryCity_List1 ro= fromObjectArray(oaylist.get(i));
            rlst.add(ro);
        }
        return rlst;
    }
}