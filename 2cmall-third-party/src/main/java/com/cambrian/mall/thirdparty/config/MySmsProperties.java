package com.cambrian.mall.thirdparty.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author kuma 2021-01-02
 */
@Data
@ConfigurationProperties(prefix = "mall.third.sms")
public class MySmsProperties {
    private String host;
    private String path;
    private String method;
    private String appCode;
    private String smsSignId;
    private String templateId;
}
