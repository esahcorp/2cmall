package com.cambrian.mall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cambrian.common.utils.PageUtils;
import com.cambrian.common.utils.Query;
import com.cambrian.mall.product.dao.SkuInfoDao;
import com.cambrian.mall.product.entity.SkuImagesEntity;
import com.cambrian.mall.product.entity.SkuInfoEntity;
import com.cambrian.mall.product.entity.SpuInfoDescEntity;
import com.cambrian.mall.product.service.*;
import com.cambrian.mall.product.vo.SkuItemSaleAttrVO;
import com.cambrian.mall.product.vo.SkuItemVO;
import com.cambrian.mall.product.vo.SpuItemAttrGroupVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;


@Service("skuInfoService")
public class SkuInfoServiceImpl extends ServiceImpl<SkuInfoDao, SkuInfoEntity> implements SkuInfoService {

    private final SkuImagesService imagesService;
    private final SpuInfoDescService spuInfoDescService;
    private final AttrGroupService attrGroupService;
    private final SkuSaleAttrValueService skuSaleAttrValueService;

    public SkuInfoServiceImpl(SkuImagesService imagesService,
                              SpuInfoDescService spuInfoDescService,
                              AttrGroupService attrGroupService,
                              SkuSaleAttrValueService skuSaleAttrValueService) {
        this.imagesService = imagesService;
        this.spuInfoDescService = spuInfoDescService;
        this.attrGroupService = attrGroupService;
        this.skuSaleAttrValueService = skuSaleAttrValueService;
    }

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuInfoEntity> page = this.page(
                new Query<SkuInfoEntity>().getPage(params),
                new QueryWrapper<SkuInfoEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils queryPageByCondition(Map<String, Object> params) {
        QueryWrapper<SkuInfoEntity> queryWrapper = new QueryWrapper<>();
        /*
            key:
            catelogId: 0
            brandId: 0
            min: 0
            max: 0
         */
        String key = (String) params.get("key");
        if (StringUtils.isNoneEmpty(key)) {
            queryWrapper.and(wp -> wp.eq("sku_id", key).or().like("sku_name", key));
        }

        String catalogId = (String) params.get("catelogId");
        if (StringUtils.isNoneEmpty(catalogId) && !"0".equals(catalogId)) {
            queryWrapper.eq("catalog_id", catalogId);
        }

        String brandId = (String) params.get("brandId");
        if (StringUtils.isNoneEmpty(brandId) && !"0".equals(brandId)) {
            queryWrapper.eq("brand_id", brandId);
        }

        String min = (String) params.get("min");
        if (StringUtils.isNoneEmpty(min)) {
            queryWrapper.ge("price", min);
        }

        String max = (String) params.get("max");
        if (StringUtils.isNoneEmpty(max) && new BigDecimal(max).compareTo(BigDecimal.ZERO) >= 0) {
            queryWrapper.le("price", max);
        }
        IPage<SkuInfoEntity> page = this.page(
                new Query<SkuInfoEntity>().getPage(params),
                queryWrapper
        );

        return new PageUtils(page);
    }

    @Override
    public List<SkuInfoEntity> getSkuBySpuId(Long spuId) {
        return this.list(new QueryWrapper<SkuInfoEntity>().eq("spu_id", spuId));
    }

    @Override
    public SkuItemVO item(Long skuId) {
        SkuItemVO vo = new SkuItemVO();
        //
        // 1. sku 基本信息获取 pms_sku_info
        // ------------------------------------------------------------------------------
        SkuInfoEntity info = getById(skuId);
        vo.setInfo(info);

        Long spuId = info.getSpuId();
        Long catalogId = info.getCatalogId();

        //
        // 2. sku 图片信息获取 pms_sku_images
        // ------------------------------------------------------------------------------
        List<SkuImagesEntity> images = imagesService.listImagesBySkuId(skuId);
        vo.setImages(images);

        //
        // 3. spu 销售属性组合
        // ------------------------------------------------------------------------------
        List<SkuItemSaleAttrVO> saleAttrs = skuSaleAttrValueService.listSaleAttrWithValueListBySpuId(spuId);
        vo.setSaleAttrs(saleAttrs);

        //
        // 4. spu 的介绍 pms_spu_info_desc
        // ------------------------------------------------------------------------------
        SpuInfoDescEntity spuDesc = spuInfoDescService.getById(spuId);
        vo.setDescription(spuDesc);

        //
        // 5. 获取 spu 的规格参数信息
        // ------------------------------------------------------------------------------
        List<SpuItemAttrGroupVO> groups = attrGroupService.listGroupWithAttrValue(catalogId, spuId);
        vo.setAttrGroups(groups);

        return vo;
    }

}