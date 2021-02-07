package com.boring.dal.test;

import com.boring.dal.json.GsonUtil;

import java.io.BufferedReader;
import java.io.FileReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParseSucc {
    private static String line="[2020-12-30 08:08:09.493]-[INFO]-[mbp-common-77cfcd9c7-nvwpc]-[XNIO-1 task-74]-[cb87cc2aff7c5850]-[cn.com.yusys.yusp.commons.fee.core.api.rest.FeeAccessController.commonAccess:155]-Process input: url=/api/risk/riskReport,flowId=riskReport,request={\"deviceType\":\"Apple\",\"sysId\":\"100000\",\"loginType\":\"0\",\"deviceInfo\":\"ODIxNzk1QjM4Mjg4NEJENzhGMDJFMjI0MkIwODlBRjJkYTI5OWEyMzM0NTUxZjZjMDQwMzk5MTNlMmQzZTMzZDAxF8pqmFSPrqVNkgOnkgFeCY9iYXMQPsJcyXIlaPVXp4cs3/ppsXh4ynUG+yzBX1asib66K13O0di7Mmht8MlNwiaid/rbIS+wj1t9D2wvo9dA6KzwKxSePJ6mQoOD739JUZ53SV+rkhXYROJgQ2yqK9rKCWOOcxWmq6J6i205LuBDipkSCxvdaE6xpEiO/j+5p9qNnrPwp7HgrmshgbwvJG+M6IR2bJHwbu4i4pFmkWleRYRMlma3IMoI9ve/F0eUQRwArstXJozJta2xXlaqytMv9NgtJJINRVZ9604WIvOElqyGvMOUHAefHB6UtjTpFvMjrNLBLoH5gK2WeW6jX6OiDJKxArHTZHKSRQrH4q+3w6WS9Z6Gybjru2JvGk2YYjajr4AyrW7+4s8+X7kl8QuBPn1fiP+lofb/O9+bhVUD6GqDvXq7iKHEEsIFPUL/6n0QkyS4wkqZw4f9T5Qqy7MRL23016HRDvLo4C9adkTaHbzcuumhbtGVaPEPP5GduxsFoxw+gca50zknEqYPuJa8L/TO1cpczfWBN9l1JuPWzmbT15ktwrJd82/z7Gz8f1jSWdoNtVgH0Y5YIy4Mf+k6RA+tUB71U7Mhh9UTYoerSRUvLfv72r5z4jzHBkyZOTYyciz9z+jizZPpnBz8vAwlbtxo60KlDzqvXExVudFmDc76Loj+Y5BEipTP0YoT6Th6Gx6WltDwbDP3ukwl1UNMHTi9RHnfpcNBr/otwtPqYbXTdLtOtadTy0LJbreZuylXWDVyZbcV0XeOlPMq2QjnQVmaOT/MnuewJJHsvwPIbhikKmpuSB6vWxhbsbcLdtxvQbYQTEf/vMGT8HrfVps/wA4Wsnwwa7yScah/8cMs9CQElsyZYvnxGKHY1dsnMujsM/f6+xfnDveqEu2yPpvlwZiXO+vRUj5f1EdANesGMEgZE+/etkXs41p/8Y1ZLTl/tzSlWSHOUASKalHQ50AGtmYdajIam30qgipRvL4fxdV//x9BAZztiwljtReM1AijvzBVFKuzc/Sfww096sCMTvIuNLIq5+0EZGutaT2cErxXMET0A/dhUqIkwaemK31yEeSxQ2sRdb043GHqwjepskH3bR+LwSTrE4r1CHX5j24ZNkLVIdmmiof3Tr3canTphcpgzG4qfxBwa5Xnv5tgUkrt+AIoz/eUQa9OpVGvEm0id77JHTsaGlluk47FlEAk/SBjwKS30pS4dhjt0rtb3SWrayrR5MURTUuXy7mTTa1cZmwNXNDytkCQFbfQppqmRsxz0CHopdgiu9jiCziEDukpkgZbDkmXySu6Kh+zc37vb/vbfm69KpFyD6j8rYLruiLUKWceS8vffBdnrF7D1MVlWWvsvAN76NLg1yIzMk6/LwgtmBsEU1Bjtueos6IIuQP4teBFcdO0EvryFX05/6d44h94eW2UXu1KThPuhrhwgvRtR+961MM3xeuv7jHtgC5LORukkDYkXqBBapAKqjcoVBp9eW2U/Bemej/IV7qWjDDPFVC52mLB1jL/x3Q29L/T6nF7HyCF96Z/uEH+DW9UwUw9lB4QfODtrhQ=\",\"phoneNo\":\"15531998818\",\"randomNum\":\"4AF16B73FE6715F7\",\"password\":\"BC1+tG4a94H0m8PUeuAX7VFt7hNg/Wm6lIY/JJYUxq1eQaJpNvsEoL1LDQ/BYa+eSA6RajA3pHbQvwqemJnbYy+Fy3cZKx5JHWyq14tgvfNDsFl+dSxpcIr9FNdSrIv7p2TUUxk0IadlKZtGtYb5s2fIGeJdH15ahwbF89C671HV\",\"grant_type\":\"password\",\"securityType\":\"01\",\"clientIp\":\"192.168.0.104\",\"rphoneNo\":\"15531998818\",\"userType\":\"1\",\"username\":\"15531998818\",\"rcrdtNo\":\"130503199205080638\",\"rcrdtTp\":\"0100\",\"rcustNo\":\"P100324002\",\"apiCode\":\"ZJK10001\",\"scene\":\"ZJK10001\",\"riskFlowNo\":\"GMBP-202012300038466513\",\"time\":\"1609286887\",\"state\":\"1\"}";
    public static void main(String[] args) throws Exception {
        HashMap<String,Object> all=new HashMap<>();

        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date begin=sdf.parse("2020-12-30 17:20:00");
        Date end=sdf.parse("2020-12-30 17:40:00");

        String file="d:/tmp/succ.log";
        String line=null;
        BufferedReader br=new BufferedReader(new FileReader(file));
        while(null!=(line=br.readLine())){
            String cc = getCustomerPhoneNo(line, begin, end);
            if(cc!=null)
                all.put(cc,new Object());
        }
        System.out.println(all.size());

    }

    public static String getCustomerPhoneNo(String line, Date begin, Date end) throws ParseException {
        String ts = line.split("]-\\[")[0];
        SimpleDateFormat sdf=new SimpleDateFormat("[yyyy-MM-dd HH:mm:ss.SSS");
        Date tsd = sdf.parse(ts);
        if(tsd.getTime()<begin.getTime()||tsd.getTime()>end.getTime())
            return null;
        Pattern p = Pattern.compile(".*request=(\\{.*\\})");
        Matcher m = p.matcher(line);
        if(m.matches()){
            CustomerInfo c = GsonUtil.fromJson(m.group(1), CustomerInfo.class);
            return c.rphoneNo;
        }
        return null;
    }
}

class CustomerInfo{
    public String rphoneNo;
}
