package com.rpc.client;


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

import java.util.List;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.boring.dal")
@ComponentScan(basePackages = {"com.boring.dal", "com.rpc.client"})
@RestController
public class RpcClientTest {

    @Autowired
    private TestAutoRpc testAutoRpc;

    public static void main(String[] args) throws Exception {
        ConfigurableApplicationContext context = SpringApplication.run(RpcClientTest.class, args);
    }

    @GetMapping("/testclient")
    public String test() throws Exception {
        Object city = testAutoRpc.get(16);
        List<Object[]> c2 = testAutoRpc.getTCity_List1("Abha", 0, 1);
        return "testok:" + new Gson().toJson(city)+", "+new Gson().toJson(c2);
    }
}
