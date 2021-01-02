package com.cambrian.mall.thirdparty.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author kuma 2021-01-02
 */
@FeignClient(name = "sms-api", url = "${mall.third.sms.host}")
public interface SmsFeignService {

    @GetMapping(value = "${mall.third.sms.path:/}")
    String send();
}
