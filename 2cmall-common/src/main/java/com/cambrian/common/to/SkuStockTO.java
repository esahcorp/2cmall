package com.cambrian.common.to;

import lombok.Data;

/**
 * @author kuma 2020-11-20
 */
@Data
public class SkuStockTO {

    private Long skuId;

    private Long stock;

    private Boolean hasStock;
}
