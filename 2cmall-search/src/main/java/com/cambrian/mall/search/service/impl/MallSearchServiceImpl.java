package com.cambrian.mall.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.cambrian.common.to.es.SkuEsModel;
import com.cambrian.mall.search.config.MallElasticsearchConfiguration;
import com.cambrian.mall.search.constant.ElasticsearchConstant;
import com.cambrian.mall.search.service.MallSearchService;
import com.cambrian.mall.search.vo.SearchParam;
import com.cambrian.mall.search.vo.SearchResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.NestedQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.MultiBucketsAggregation;
import org.elasticsearch.search.aggregations.bucket.nested.Nested;
import org.elasticsearch.search.aggregations.bucket.nested.NestedAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.cambrian.common.to.es.SkuEsModel.AttrEsModel.*;
import static com.cambrian.common.to.es.SkuEsModel.*;

/**
 * @author kuma 2020-12-02
 */
@Slf4j
@Service
public class MallSearchServiceImpl implements MallSearchService {

    private static final String AGG_BRAND = "brand_agg";
    private static final String AGG_BRAND_NAME = "brand_name_agg";
    private static final String AGG_BRAND_IMG = "brand_img_agg";
    private static final String AGG_CATALOG = "catalog_agg";
    private static final String AGG_CATALOG_NAME = "catalog_name_agg";
    private static final String AGG_ATTR = "attr_agg";
    private static final String AGG_ATTR_ID = "attr_id_agg";
    private static final String AGG_ATTR_NAME = "attr_name_agg";
    private static final String AGG_ATTR_VALUE = "attr_value_agg";

    private final RestHighLevelClient client;

    public MallSearchServiceImpl(RestHighLevelClient client) {
        this.client = client;
    }

    @Override
    public SearchResult search(SearchParam searchParam) {

        // 1. 构建请求数据
        SearchRequest searchRequest = buildSearchRequest(searchParam);
        SearchResult searchResult = null;
        try {
            // 2. 执行请求
            SearchResponse response = client.search(searchRequest, MallElasticsearchConfiguration.COMMON_OPTIONS);
            log.debug("SKU 搜索结果：{}", response.toString());
            // 3. 分析并转换数据
            searchResult = buildSearchResult(response, searchParam);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return searchResult;
    }

    /**
     * 构建 Elasticsearch 请求 dsl，参照 resource/dsl/product_search_request.json
     *
     * @param searchParam {@link SearchParam}
     * @return {@link SearchRequest}，es 封装的请求结构
     */
    private SearchRequest buildSearchRequest(SearchParam searchParam) {
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

        //===============================================================================
        //  1. 构建 query 部分 - bool
        //===============================================================================

        BoolQueryBuilder boolQuery = buildQueryPart(searchParam);
        sourceBuilder.query(boolQuery);

        //===============================================================================
        //  2. 排序规则
        //===============================================================================

        if (StringUtils.isNotBlank(searchParam.getSort())) {
            String[] sortTags = searchParam.getSort().split("_");
            if (sortTags.length == 2) {
                sourceBuilder.sort(sortTags[0], SortOrder.fromString(sortTags[1]));
            }
        }

        //===============================================================================
        //  3. 分页信息
        //===============================================================================

        int from = (searchParam.getPageNum() - 1) * searchParam.getPageSize();
        sourceBuilder.from(from);
        sourceBuilder.size(searchParam.getPageSize());

        //===============================================================================
        //  4. 高亮
        //===============================================================================

        if (StringUtils.isNotBlank(searchParam.getKeyword())) {
            HighlightBuilder highlightBuilder = new HighlightBuilder();
            highlightBuilder.field(FIELD_SKU_TITLE);
            highlightBuilder.preTags(searchParam.getHighlightPreTag());
            highlightBuilder.postTags(searchParam.getHighlightPostTag());
            sourceBuilder.highlighter(highlightBuilder);
        }

        //===============================================================================
        //  5. 聚合分析
        //===============================================================================

        // 5.1 筛选出查询结果包含的所有品牌
        TermsAggregationBuilder brandAgg = AggregationBuilders.terms(AGG_BRAND).field(FIELD_BRAND_ID).size(50);
        brandAgg.subAggregation(AggregationBuilders.terms(AGG_BRAND_IMG).field(FIELD_BRAND_IMG).size(1));
        brandAgg.subAggregation(AggregationBuilders.terms(AGG_BRAND_NAME).field(FIELD_BRAND_NAME).size(1));
        sourceBuilder.aggregation(brandAgg);
        // 5.2 筛选出查询结果包含的分类
        TermsAggregationBuilder catalogAgg = AggregationBuilders.terms(AGG_CATALOG).field(FIELD_CATALOG_ID).size(20);
        catalogAgg.subAggregation(AggregationBuilders.terms(AGG_CATALOG_NAME).field(FIELD_CATALOG_NAME).size(1));
        sourceBuilder.aggregation(catalogAgg);
        // 5.3 筛选出查询结果包含的属性及其可选属性值
        NestedAggregationBuilder attrAgg = AggregationBuilders.nested(AGG_ATTR, FIELD_ATTRS);
        // 属性聚合应该为嵌套聚合
        TermsAggregationBuilder attrIdAgg = AggregationBuilders.terms(AGG_ATTR_ID).field(FIELD_ATTR_ID).size(50);
        attrIdAgg.subAggregation(AggregationBuilders.terms(AGG_ATTR_NAME).field(FIELD_ATTR_NAME).size(1));
        attrIdAgg.subAggregation(AggregationBuilders.terms(AGG_ATTR_VALUE).field(FIELD_ATTR_VALUE).size(50));
        attrAgg.subAggregation(attrIdAgg);
        sourceBuilder.aggregation(attrAgg);

        log.debug("SKU 搜索 DSL: {}", sourceBuilder.toString());
        return new SearchRequest(new String[]{ElasticsearchConstant.PRODUCT_INDEX}, sourceBuilder);
    }

    private BoolQueryBuilder buildQueryPart(SearchParam searchParam) {
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        // 1.1 must - 关键词模糊匹配
        if (StringUtils.isNotBlank(searchParam.getKeyword())) {
            boolQuery.must(QueryBuilders.matchQuery(FIELD_SKU_TITLE, searchParam.getKeyword()));
        }
        // 1.2 filter 三级分类 id
        if (searchParam.getCatalog3Id() != null) {
            boolQuery.filter(QueryBuilders.termQuery(FIELD_CATALOG_ID, searchParam.getCatalog3Id()));
        }
        // 1.3 filter 品牌 id
        if (!CollectionUtils.isEmpty(searchParam.getBrandId())) {
            boolQuery.filter(QueryBuilders.termsQuery(FIELD_BRAND_ID, searchParam.getBrandId()));
        }
        // 1.4 filter 属性集合
        if (!CollectionUtils.isEmpty(searchParam.getAttrs())) {
            for (String as : searchParam.getAttrs()) {
                // 每一种属性对应一个 filter nested bool
                String[] s = as.split(SearchParam.VALUE_TYPE_DELIMITER);
                if (s.length == 2) {
                    BoolQueryBuilder attrBoolQuery = QueryBuilders.boolQuery();
                    // 属性 id
                    attrBoolQuery.must(QueryBuilders.termQuery(FIELD_ATTR_ID, s[0]));
                    String[] values = s[1].split(SearchParam.VALUE_LIST_DELIMITER);
                    // 属性值
                    attrBoolQuery.must(QueryBuilders.termsQuery(FIELD_ATTR_VALUE, values));
                    NestedQueryBuilder nestedQuery = QueryBuilders.nestedQuery(FIELD_ATTRS, attrBoolQuery, ScoreMode.None);
                    boolQuery.filter(nestedQuery);
                }
            }
        }
        // 1.5 filter 是否有库存，只存在【不考虑库存】和【有库存】两种情况
        if (searchParam.getHasStock() != null && searchParam.getHasStock() == 1) {
            boolQuery.filter(QueryBuilders.termQuery(FIELD_HAS_STOCK, "true"));
        }
        // 1.6 range 价格区间，按照 "_" 切割
        if (StringUtils.isNotBlank(searchParam.getSkuPrice())) {
            RangeQueryBuilder rangeQuery = buildPriceQuery(searchParam.getSkuPrice());
            boolQuery.filter(rangeQuery);
        }
        return boolQuery;
    }

    private RangeQueryBuilder buildPriceQuery(String skuPrice) {
        RangeQueryBuilder rangeQuery = QueryBuilders.rangeQuery(FIELD_SKU_PRICE);
        String[] prices = skuPrice.split(SearchParam.VALUE_TYPE_DELIMITER);
        if (prices.length == 2) {
            rangeQuery.gte(prices[0]).lte(prices[1]);
        } else if (prices.length == 1) {
            if (skuPrice.startsWith(SearchParam.VALUE_TYPE_DELIMITER)) {
                rangeQuery.lte(prices[0]);
            } else if (skuPrice.endsWith(SearchParam.VALUE_TYPE_DELIMITER)) {
                rangeQuery.gte(prices[0]);
            }
        }
        return rangeQuery;
    }

    private SearchResult buildSearchResult(SearchResponse response, SearchParam searchParam) {
        SearchResult result = new SearchResult();
        SearchHits hits = response.getHits();

        //===============================================================================
        //  1. 从返回结果中提取所有商品信息
        //===============================================================================

        List<SkuEsModel> products = new ArrayList<>();
        if (!ArrayUtils.isEmpty(hits.getHits())) {
            for (SearchHit hit : hits.getHits()) {
                String sourceString = hit.getSourceAsString();
                SkuEsModel skuEsModel = JSON.parseObject(sourceString, SkuEsModel.class);
                if (StringUtils.isNotBlank(searchParam.getKeyword())) {
                    HighlightField highlightField = hit.getHighlightFields().get(FIELD_SKU_TITLE);
                    Text[] fragments = highlightField.getFragments();
                    skuEsModel.setSkuTitle(fragments[0].string());
                }
                products.add(skuEsModel);
            }
        }
        result.setProducts(products);

        Aggregations aggregations = response.getAggregations();

        //===============================================================================
        //  2. 从查询结果中提取所有涉及的品牌信息
        //===============================================================================

        Terms brandAggregation = aggregations.get(AGG_BRAND);
        List<SearchResult.BrandVO> brands = new ArrayList<>();
        for (Terms.Bucket bucket : brandAggregation.getBuckets()) {
            Long brandId = bucket.getKeyAsNumber().longValue();
            // 从子聚合中取出品牌名称和品牌图片
            Aggregations subAggregations = bucket.getAggregations();
            Terms brandNameAgg = subAggregations.get(AGG_BRAND_NAME);
            Terms.Bucket nameBucket = brandNameAgg.getBuckets().get(0);
            String brandName = nameBucket.getKeyAsString();
            Terms brandImgAgg = subAggregations.get(AGG_BRAND_IMG);
            Terms.Bucket imgBucket = brandImgAgg.getBuckets().get(0);
            String brandImg = imgBucket.getKeyAsString();
            SearchResult.BrandVO brand = new SearchResult.BrandVO(brandId, brandName, brandImg);
            brands.add(brand);
        }
        result.setBrands(brands);

        //===============================================================================
        //  3. 从查询结果中提取所有涉及的类别信息
        //===============================================================================

        Terms catalogAggregation = aggregations.get(AGG_CATALOG);
        List<SearchResult.CatalogVO> catalogs = new ArrayList<>();
        for (Terms.Bucket bucket : catalogAggregation.getBuckets()) {
            Long catalogId = bucket.getKeyAsNumber().longValue();
            Aggregations subAggregations = bucket.getAggregations();
            Terms catalogNameAgg = subAggregations.get(AGG_CATALOG_NAME);
            Terms.Bucket nameBucket = catalogNameAgg.getBuckets().get(0);
            String catalogName = nameBucket.getKeyAsString();
            SearchResult.CatalogVO catalog = new SearchResult.CatalogVO(catalogId, catalogName);
            catalogs.add(catalog);
        }
        result.setCatalogs(catalogs);

        //===============================================================================
        //  4. 从查询结果中提取所有涉及到的属性信息
        //===============================================================================

        List<SearchResult.AttrVO> attrs = new ArrayList<>();
        Nested attrNestedAgg = aggregations.get(AGG_ATTR);
        Terms attrIdTerms = attrNestedAgg.getAggregations().get(AGG_ATTR_ID);
        for (Terms.Bucket bucket : attrIdTerms.getBuckets()) {
            Long attrId = bucket.getKeyAsNumber().longValue();
            Terms attrNameTerms = bucket.getAggregations().get(AGG_ATTR_NAME);
            String attrName = attrNameTerms.getBuckets().get(0).getKeyAsString();
            Terms attrValueTerms = bucket.getAggregations().get(AGG_ATTR_VALUE);
            List<String> values = attrValueTerms.getBuckets().stream()
                    .map(MultiBucketsAggregation.Bucket::getKeyAsString).collect(Collectors.toList());
            SearchResult.AttrVO attr = new SearchResult.AttrVO(attrId, attrName, values);
            attrs.add(attr);
        }
        result.setAttrs(attrs);

        //===============================================================================
        //  5. 页码 总记录数
        //===============================================================================
        long totalHits = hits.getTotalHits().value;
        result.setTotal(totalHits);
        result.setPageNum(searchParam.getPageNum());
        long arrangement = totalHits / searchParam.getPageSize();
        long totalPages = (int) totalHits % searchParam.getPageSize() == 0 ? arrangement : arrangement + 1;
        result.setTotalPages((int) totalPages);

        return result;
    }
}
