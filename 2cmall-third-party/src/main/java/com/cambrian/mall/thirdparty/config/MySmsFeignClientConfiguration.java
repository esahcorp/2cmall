package com.cambrian.mall.thirdparty.config;

import feign.auth.BasicAuthRequestInterceptor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author kuma 2021-01-02
 */
@Configuration
@EnableConfigurationProperties(MySmsProperties.class)
public class MySmsFeignClientConfiguration {

    @Bean
    public BasicAuthRequestInterceptor basicAuthRequestInterceptor() {
        return new BasicAuthRequestInterceptor("test", "test");
    }
}
