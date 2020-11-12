package com.cambrian.common.to;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author kuma 2020-10-30
 */
@Data
public class SpuBoundsTO {

    /**
     *
     */
    private Long spuId;
    /**
     * 成长积分
     */
    private BigDecimal growBounds;
    /**
     * 购物积分
     */
    private BigDecimal buyBounds;

}
