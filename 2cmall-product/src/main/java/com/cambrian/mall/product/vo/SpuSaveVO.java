package com.cambrian.mall.product.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class SpuSaveVO{

	private String spuDescription;
	private String spuName;
	private List<String> images;
	private Long catalogId;
	private List<SkuItem> skus;
	private List<BaseAttrItem> baseAttrs;
	private Long brandId;
	/**
     * 积分
	 */
	private Bounds bounds;
	private BigDecimal weight;
	private List<String> decript;
	private Integer publishStatus;
}