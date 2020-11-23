package com.cambrian.mall.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.cambrian.common.to.es.SkuEsModel;
import com.cambrian.mall.search.config.MallElasticsearchConfiguration;
import com.cambrian.mall.search.constant.ElasticsearchConstant;
import com.cambrian.mall.search.service.ElasticsearchProductService;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author kuma 2020-11-20
 */
@Slf4j
@Service
public class ElasticsearchProductServiceImpl implements ElasticsearchProductService {

    private final RestHighLevelClient restHighLevelClient;

    public ElasticsearchProductServiceImpl(RestHighLevelClient restHighLevelClient) {
        this.restHighLevelClient = restHighLevelClient;
    }

    @Override
    public boolean saveSkuInfo(List<SkuEsModel> skuEsModels) throws IOException {
        BulkRequest bulkRequest = new BulkRequest();
        for (SkuEsModel skuEsModel : skuEsModels) {
            IndexRequest request = new IndexRequest(ElasticsearchConstant.PRODUCT_INDEX);
            request.id(String.valueOf(skuEsModel.getSkuId()));
            String source = JSON.toJSONString(skuEsModel);
            request.source(source, XContentType.JSON);

            bulkRequest.add(request);
        }
        BulkResponse response = restHighLevelClient.bulk(bulkRequest, MallElasticsearchConfiguration.COMMON_OPTIONS);
        boolean hasFailures = response.hasFailures();
        if (hasFailures) {
            List<String> ids = Arrays.stream(response.getItems())
                    .map(BulkItemResponse::getId)
                    .collect(Collectors.toList());
            log.error("商品上架错误，{}", ids);
        }
        return !hasFailures;
    }
}
