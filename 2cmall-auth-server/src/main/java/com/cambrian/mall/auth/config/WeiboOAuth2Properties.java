package com.cambrian.mall.auth.config;

import lombok.Data;
import lombok.var;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

/**
 * @author kuma 2021-01-05
 */
@Data
@Component
@ConfigurationProperties(prefix = "ccmall.oauth2.weibo")
public class WeiboOAuth2Properties {

    private String appKey;
    private String appSecret;
    private String apiHost;
    private String tokenApiPath;

    public MultiValueMap<String, String> buildTokenParam(String code) {
        var param = new LinkedMultiValueMap<String, String>();
        param.add("client_id", appKey);
        param.add("client_secret", appSecret);
        param.add("grant_type", "authorization_code");
        param.add("redirect_uri", "http://auth.2cmall.com/oauth2.0/weibo/success");
        param.add("code", code);
        return param;
    }

    public String tokenUrl() {
        return apiHost + tokenApiPath;
    }
}
