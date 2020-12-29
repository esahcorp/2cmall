package com.cambrian.mall.product.app;

import com.cambrian.mall.product.service.SkuInfoService;
import com.cambrian.mall.product.vo.SkuItemVO;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @author kuma 2020-12-10
 */
@Controller
public class ItemController {

    private final SkuInfoService skuInfoService;

    public ItemController(SkuInfoService skuInfoService) {
        this.skuInfoService = skuInfoService;
    }

    @GetMapping("/{skuId}.html")
    public String skuItem(@PathVariable("skuId") Long skuId, Model model) {
        SkuItemVO sku = skuInfoService.item(skuId);
        model.addAttribute("item", sku);
        return "item";
    }

}
