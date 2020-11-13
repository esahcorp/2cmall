package com.cambrian.mall.ware.vo;

import lombok.Data;

import java.util.List;

/**
 * @author kuma 2020-11-12
 */
@Data
public class MergeVO {

    private Long purchaseId;
    private List<Long> items;
}
