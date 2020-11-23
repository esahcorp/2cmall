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

    private List<AttrEsMode> attrs;

    @Data
    public static class AttrEsMode {

        private Long attrId;

        private String attrName;

        private String attrValue;
    }

}
