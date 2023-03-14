package com.fram.OrderService.config;


import com.fram.OrderService.external.decoder.CustoErrorDecoder;
import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/*
* This class will serve as a configuration for feign to
* propagate errors from product service to order service
* so that we can have a response
*
* */
@Configuration
public class FeignConfig {

    @Bean
    ErrorDecoder errorDecoder(){
        return new CustoErrorDecoder();
    }

}
