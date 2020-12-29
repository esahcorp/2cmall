package com.cambrian.mall.product.vo;

import com.cambrian.mall.product.entity.SkuImagesEntity;
import com.cambrian.mall.product.entity.SkuInfoEntity;
import com.cambrian.mall.product.entity.SpuInfoDescEntity;
import lombok.Data;

import java.util.List;

/**
 * 商城 sku 详情 VO
 *
 * @author kuma 2020-12-16
 */
@Data
public class SkuItemVO {

    /**
     * sku 基本信息
     */
    private SkuInfoEntity info;

    /**
     * sku 图片信息
     */
    private List<SkuImagesEntity> images;


    /**
     * 相同 spu 下的所有 sku 销售属性组合
     */
    private List<SkuItemSaleAttrVO> saleAttrs;

    /**
     * spu 介绍
     */
    private SpuInfoDescEntity description;

    /**
     * spu 规格参数分组
     */
    private List<SpuItemAttrGroupVO> attrGroups;

}
