package com.cambrian.mall.auth.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * @author kuma 2021-01-05
 */
@Configuration
public class MyFeignConfiguration {

    @Bean
    public RestTemplate restTemplate(ObjectMapper objectMapper) {
        return new RestTemplate();
    }
}
