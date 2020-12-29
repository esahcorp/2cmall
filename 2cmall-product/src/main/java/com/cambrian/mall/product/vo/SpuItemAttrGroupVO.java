package com.cambrian.mall.product.vo;

import lombok.Data;

import java.util.List;

/**
 * @author kuma 2020-12-29
 */
@Data
public class SpuItemAttrGroupVO {
    private String attrGroupName;
    private List<SkuAttrItem> attrs;
}
