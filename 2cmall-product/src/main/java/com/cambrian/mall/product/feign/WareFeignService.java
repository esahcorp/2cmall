package com.cambrian.mall.product.feign;

import com.cambrian.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @author kuma 2020-11-20
 */
@FeignClient(value = "2cmall-ware", path = "/ware")
public interface WareFeignService {

    @PostMapping("/waresku/stock")
    R getSkuStock(@RequestBody List<Long> skuIds);
}
