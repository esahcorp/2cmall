package com.cambrian.mall.ware.feign;

import com.cambrian.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author kuma 2020-11-13
 */
@FeignClient("2cmall-product")
@RequestMapping("/product")
public interface ProductFeignService {

    @RequestMapping("/skuinfo/info/{skuId}")
    R info(@PathVariable("skuId") Long skuId);

}
