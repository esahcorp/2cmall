package com.cambrian.mall.auth.feign;

import com.cambrian.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author kuma 2021-01-02
 */
@FeignClient("2cmall-third-party")
public interface ThirdPartFeignService {

    @GetMapping("/sms/verification")
    R sendSmsVerificationCode(@RequestParam("verificationCode") String verificationCode,
                              @RequestParam("phoneNumber") String phoneNumber);
}
