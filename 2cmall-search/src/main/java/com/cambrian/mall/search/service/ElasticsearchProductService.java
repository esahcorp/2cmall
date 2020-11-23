package com.cambrian.mall.search.service;

import com.cambrian.common.to.es.SkuEsModel;

import java.io.IOException;
import java.util.List;

/**
 * @author kuma 2020-11-20
 */
public interface ElasticsearchProductService {

    /**
     * 将 sku 信息保存到 Elasticsearch 中，返回结果代表是否所有数据都成功保存
     *
     * @param skuEsModels list of es model
     * @return true if all data save success, false if any failure
     * @throws IOException es high level client
     */
    boolean saveSkuInfo(List<SkuEsModel> skuEsModels) throws IOException;
}
