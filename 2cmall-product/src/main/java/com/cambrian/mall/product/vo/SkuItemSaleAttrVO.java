package com.cambrian.mall.product.vo;

import lombok.Data;

import java.util.List;

/**
 * @author kuma 2020-12-29
 */
@Data
public class SkuItemSaleAttrVO {
    private Long attrId;
    private String attrName;
    private List<AttrValueWithSkusVO> attrValues;
}
