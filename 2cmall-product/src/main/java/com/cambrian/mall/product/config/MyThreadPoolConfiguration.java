package com.cambrian.mall.product.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author kuma 2020-12-30
 */
@Configuration
@EnableConfigurationProperties(MyThreadPoolProperties.class)
public class MyThreadPoolConfiguration {

    @Bean
    public ThreadPoolExecutor threadPoolExecutor(MyThreadPoolProperties threadPoolProperties) {
        return new ThreadPoolExecutor(threadPoolProperties.getCoreSize(), threadPoolProperties.getMaxSize(),
                threadPoolProperties.getKeepAliveTime(), TimeUnit.SECONDS,
                new LinkedBlockingDeque<>(100000),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.AbortPolicy());
    }
}
