package com.cambrian.mall.search.service;

import com.cambrian.mall.search.vo.SearchParam;
import com.cambrian.mall.search.vo.SearchResult;

/**
 * @author kuma 2020-12-02
 */
public interface MallSearchService {
    /**
     * 从 Elasticsearch 中查询商品信息
     *
     * @param searchParam 查询条件
     * @return 返回结果
     */
    SearchResult search(SearchParam searchParam);
}
