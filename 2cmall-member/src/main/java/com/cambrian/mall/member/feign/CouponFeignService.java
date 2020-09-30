package com.cambrian.mall.member.feign;

import com.cambrian.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author kuma 2020-09-30
 */
@FeignClient("2cmall-coupon")
public interface CouponFeignService {

    @GetMapping("/coupon/coupon/test")
    public R test();
}
