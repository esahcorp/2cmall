package com.cambrian.common.to;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author kuma 2020-10-30
 */
@Data
public class SkuReductionTO {

    private Long skuId;
    private Integer fullCount;
    private BigDecimal discount;
    private BigDecimal fullPrice;
    private BigDecimal reducePrice;
    private Integer priceStatus;
    private Integer countStatus;
    private List<MemberPriceItem> memberPrice;

}
