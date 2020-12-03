package com.cambrian.mall.search.vo;

import com.cambrian.common.to.es.SkuEsModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 商品搜索结果封装
 * @author kuma 2020-12-02
 */
@Data
public class SearchResult {

    private List<SkuEsModel> products;

    private Integer pageNum;
    private Long total;
    private Integer totalPages;

    private List<BrandVO> brands;
    private List<CatalogVO> catalogs;
    private List<AttrVO> attrs;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BrandVO {
        private Long brandId;
        private String brandName;
        private String brandImg;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CatalogVO {
        private Long catalogId;
        private String catalogName;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AttrVO {
        private Long attrId;
        private String attrName;
        private List<String> attrValues;
    }
}
