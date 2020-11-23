package com.cambrian.mall.search.controller;

import com.cambrian.common.to.es.SkuEsModel;
import com.cambrian.common.utils.R;
import com.cambrian.mall.search.service.ElasticsearchProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

/**
 * @author kuma 2020-11-20
 */
@Slf4j
@RestController
@RequestMapping("/search/product")
public class ElasticsearchProductController {

    @Autowired
    private ElasticsearchProductService productService;

    @PostMapping("/skus")
    public R saveSkuInfo(@RequestBody List<SkuEsModel> skuEsModels) {
        boolean allSuccess;
        try {
            allSuccess = productService.saveSkuInfo(skuEsModels);
        } catch (IOException e) {
            log.error("商品上架错误", e);
            return R.error();
        }
        return allSuccess ? R.ok() : R.error();
    }
}
