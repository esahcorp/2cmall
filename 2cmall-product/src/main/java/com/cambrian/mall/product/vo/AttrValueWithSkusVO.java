package com.cambrian.mall.product.vo;

import lombok.Data;

import java.util.List;

/**
 * @author kuma 2020-12-30
 */
@Data
public class AttrValueWithSkusVO {
    private String attrValue;
    private List<Long> skus;
}
