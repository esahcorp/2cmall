package com.cambrian.mall.product.feign;

import com.cambrian.common.to.SkuReductionTO;
import com.cambrian.common.to.SpuBoundsTO;
import com.cambrian.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author kuma 2020-09-30
 */
@FeignClient("2cmall-coupon")
public interface CouponFeignService {

    @PostMapping("/coupon/spubounds/save")
    R saveSpuBounds(@RequestBody SpuBoundsTO spuBoundsTo);

    @PostMapping("/coupon/skufullreduction/skuinfo")
    R saveSkuReduction(@RequestBody SkuReductionTO skuReductionTo);
}
