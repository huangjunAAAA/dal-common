package com.rpc.client;


import com.rpc.test.model.TCity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.boring.dal")
@ComponentScan(basePackages = {"com.boring.dal", "com.rpc.client"})
@RestController
public class RpcClientTest {

    @Autowired
    private TCityDaoWrapper tCityDaoWrapper;

    public static void main(String[] args) throws Exception {
        ConfigurableApplicationContext context = SpringApplication.run(RpcClientTest.class, args);
    }

    @GetMapping("/testclient")
    public String test() throws Exception {
        TCity city = tCityDaoWrapper.get(16);
        return "testok:" + city;
    }
}
