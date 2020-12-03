package com.cambrian.common.to.es;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * Elastisearch Sku 模型
 *
 * @author kuma 2020-11-18
 */
@Data
public class SkuEsModel {

    public static final String FIELD_SKU_TITLE = "skuTitle";
    public static final String FIELD_SKU_PRICE = "skuPrice";
    public static final String FIELD_SALE_COUNT = "saleCount";
    public static final String FIELD_HAS_STOCK = "hasStock";
    public static final String FIELD_HOT_SCORE = "hotScore";
    public static final String FIELD_BRAND_ID = "brandId";
    public static final String FIELD_BRAND_NAME = "brandName";
    public static final String FIELD_BRAND_IMG = "brandImg";
    public static final String FIELD_CATALOG_ID = "catalogId";
    public static final String FIELD_CATALOG_NAME = "catalogName";
    public static final String FIELD_ATTRS = "attrs";

    private Long skuId;

    private Long spuId;

    private String skuTitle;

    private BigDecimal skuPrice;

    private String skuImg;

    private Long saleCount;

    private Boolean hasStock;

    private Long hotScore;

    private Long brandId;

    private Long catalogId;

    private String brandName;

    private String brandImg;

    private String catalogName;

    private List<AttrEsModel> attrs;

    @Data
    public static class AttrEsModel {

        public static final String FIELD_ATTR_ID = "attrs.attrId";
        public static final String FIELD_ATTR_NAME = "attrs.attrName";
        public static final String FIELD_ATTR_VALUE = "attrs.attrValue";

        private Long attrId;

        private String attrName;

        private String attrValue;
    }

}
