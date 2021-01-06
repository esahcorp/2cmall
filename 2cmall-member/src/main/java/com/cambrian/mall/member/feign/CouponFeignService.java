package com.cambrian.mall.member.feign;

import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author kuma 2020-09-30
 */
@FeignClient("2cmall-coupon")
public interface CouponFeignService {

}
