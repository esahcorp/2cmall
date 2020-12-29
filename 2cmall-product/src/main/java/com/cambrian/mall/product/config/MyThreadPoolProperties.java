package com.cambrian.mall.product.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author kuma 2020-12-30
 */
@ConfigurationProperties(prefix = "cmall.thread-pool")
@Data
public class MyThreadPoolProperties {
    private Integer coreSize;
    private Integer maxSize;
    private Integer keepAliveTime;
}
