package com.boring.dal.remote;

import feign.RequestTemplate;
import feign.codec.EncodeException;
import feign.codec.Encoder;
import org.springframework.context.annotation.Bean;

import java.lang.reflect.Type;

public class RpcFeignCfg {

    @Bean
    public Encoder rpcEncoder(){
        return new RPCDecoder();
    }

    public static class RPCDecoder implements Encoder{


        @Override
        public void encode(Object object, Type bodyType, RequestTemplate template) throws EncodeException {
            System.out.println(bodyType);
        }
    }
}
