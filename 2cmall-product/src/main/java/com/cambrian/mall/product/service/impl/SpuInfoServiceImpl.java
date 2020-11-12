package com.cambrian.mall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cambrian.common.to.SkuReductionTO;
import com.cambrian.common.to.SpuBoundsTO;
import com.cambrian.common.utils.PageUtils;
import com.cambrian.common.utils.Query;
import com.cambrian.common.utils.R;
import com.cambrian.mall.product.dao.SpuInfoDao;
import com.cambrian.mall.product.entity.*;
import com.cambrian.mall.product.feign.CouponFeignService;
import com.cambrian.mall.product.service.*;
import com.cambrian.mall.product.vo.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service("spuInfoService")
public class SpuInfoServiceImpl extends ServiceImpl<SpuInfoDao, SpuInfoEntity> implements SpuInfoService {

    private final SpuInfoDescService spuDescService;
    private final SpuImagesService spuImagesService;
    private final ProductAttrValueService attrValueService;
    private final AttrService attrService;
    private final SkuInfoService skuInfoService;
    private final SkuImagesService skuImagesService;
    private final SkuSaleAttrValueService skuSaleAttrValueService;
    private final CouponFeignService couponFeignService;

    public SpuInfoServiceImpl(SpuInfoDescService spuDescService,
                              SpuImagesService spuImagesService,
                              ProductAttrValueService attrValueService,
                              AttrService attrService,
                              SkuInfoService skuInfoService,
                              SkuImagesService skuImagesService,
                              SkuSaleAttrValueService skuSaleAttrValueService,
                              CouponFeignService couponFeignService) {
        this.spuDescService = spuDescService;
        this.spuImagesService = spuImagesService;
        this.attrValueService = attrValueService;
        this.attrService = attrService;
        this.skuInfoService = skuInfoService;
        this.skuImagesService = skuImagesService;
        this.skuSaleAttrValueService = skuSaleAttrValueService;
        this.couponFeignService = couponFeignService;
    }

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                new QueryWrapper<SpuInfoEntity>()
        );

        return new PageUtils(page);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void saveSpuInfo(SpuSaveVO spu) {

        // 1。保存基本信息 pms_spu_info
        SpuInfoEntity spuInfoEntity = new SpuInfoEntity();
        BeanUtils.copyProperties(spu, spuInfoEntity);
        spuInfoEntity.setCreateTime(new Date());
        spuInfoEntity.setUpdateTime(new Date());
        this.saveBaseSpuInfo(spuInfoEntity);

        Long spuId = spuInfoEntity.getId();

        // 2。保存spu描述图片 pms_spu_info_desc
        List<String> desc = spu.getDecript();
        if (!CollectionUtils.isEmpty(desc)) {
            SpuInfoDescEntity spuInfoDescEntity = new SpuInfoDescEntity();
            spuInfoDescEntity.setSpuId(spuId);
            spuInfoDescEntity.setDecript(String.join(",", desc));

            spuDescService.saveSpuDesc(spuInfoDescEntity);
        }

        // 3。 保存 spu 图片集 pms_spu_images
        List<String> images = spu.getImages();
        if (!CollectionUtils.isEmpty(images)) {
            List<SpuImagesEntity> imagesEntities = images.stream()
                    .filter(StringUtils::isNotBlank)
                    .map(url -> {
                        SpuImagesEntity spuImagesEntity = new SpuImagesEntity();
                        spuImagesEntity.setSpuId(spuId);
                        spuImagesEntity.setImgUrl(url);
                        return spuImagesEntity;
                    }).collect(Collectors.toList());

            spuImagesService.saveImagesBatch(imagesEntities);
        }

        // 4。保存 spu 的规格参数 pms_product_attr_value
        List<BaseAttrItem> spuAttrs = spu.getBaseAttrs();
        if (!CollectionUtils.isEmpty(spuAttrs)) {
            List<ProductAttrValueEntity> attrValueEntities = spuAttrs.stream().map(attr -> {
                ProductAttrValueEntity productAttrValueEntity = new ProductAttrValueEntity();
                productAttrValueEntity.setSpuId(spuId);
                productAttrValueEntity.setAttrId(attr.getAttrId());
                AttrEntity attrEntity = attrService.getById(attr.getAttrId());
                productAttrValueEntity.setAttrName(attrEntity.getAttrName());
                productAttrValueEntity.setAttrValue(attr.getAttrValues());
                productAttrValueEntity.setQuickShow(attr.getShowDesc());
                return productAttrValueEntity;
            }).collect(Collectors.toList());

            attrValueService.saveBatch(attrValueEntities);
        }

        // 保存 spu 的积分信息 sms_spu_bounds
        Bounds bounds = spu.getBounds();
        if (bounds != null) {
            SpuBoundsTO spuBoundsTo = new SpuBoundsTO();
            BeanUtils.copyProperties(bounds, spuBoundsTo);
            spuBoundsTo.setSpuId(spuId);

            R spuResult = couponFeignService.saveSpuBounds(spuBoundsTo);
            if (!spuResult.isSuccess()) {
                log.error("调用远程接口保存 spu 积分信息失败");
            }
        }

        // 5。 保存所有 sku 信息
        List<SkuItem> skus = spu.getSkus();
        for (SkuItem sku : skus) {

            // pr: 找到 sku 默认图片 url
            String defaultImageUrl = sku.getImages().stream()
                    .filter(img -> img.getDefaultImg() == 1)
                    .map(ImagesItem::getImgUrl)
                    .findFirst()
                    .orElse(null);

            // 5.1 保存sku 基本信息 pms_sku_info
            SkuInfoEntity skuInfoEntity = new SkuInfoEntity();
            BeanUtils.copyProperties(sku, skuInfoEntity);
            skuInfoEntity.setSpuId(spuId);
            skuInfoEntity.setBrandId(spuInfoEntity.getBrandId());
            skuInfoEntity.setCatalogId(spuInfoEntity.getCatalogId());
            skuInfoEntity.setSkuDefaultImg(defaultImageUrl);
            skuInfoService.save(skuInfoEntity);

            Long skuId = skuInfoEntity.getSkuId();

            // 5.2 保存 sku 图片信息 pms_spu_images
            List<ImagesItem> skuImages = sku.getImages();
            if (!CollectionUtils.isEmpty(skuImages)) {
                List<SkuImagesEntity> skuImagesEntities = skuImages.stream()
                        .filter(imagesItem -> StringUtils.isNotBlank(imagesItem.getImgUrl()))
                        .map(img -> {
                            SkuImagesEntity skuImagesEntity = new SkuImagesEntity();
                            skuImagesEntity.setSkuId(skuId);
                            skuImagesEntity.setImgUrl(img.getImgUrl());
                            skuImagesEntity.setDefaultImg(img.getDefaultImg());

                            return skuImagesEntity;
                        }).collect(Collectors.toList());

                skuImagesService.saveBatch(skuImagesEntities);
            }

            // 5.3 保存 sku 的销售属性值  pms_sku_sale_attr_value
            List<SkuAttrItem> skuAttrs = sku.getAttr();
            if (!CollectionUtils.isEmpty(skuAttrs)) {
                List<SkuSaleAttrValueEntity> saleAttrValueEntities = skuAttrs.stream().map(attr -> {
                    SkuSaleAttrValueEntity skuSaleAttrValueEntity = new SkuSaleAttrValueEntity();
                    BeanUtils.copyProperties(attr, skuSaleAttrValueEntity);
                    skuSaleAttrValueEntity.setSkuId(skuId);

                    return skuSaleAttrValueEntity;
                }).collect(Collectors.toList());

                skuSaleAttrValueService.saveBatch(saleAttrValueEntities);
            }

            // 5.4 sku 的优惠满减信息 sms_sku_full_reduction  sms_sku_ladder   sms_member_price
            if (sku.getFullCount() > 0 || sku.getFullPrice().compareTo(BigDecimal.ZERO) > 0) {
                SkuReductionTO skuReduction = new SkuReductionTO();
                skuReduction.setSkuId(skuId);
                BeanUtils.copyProperties(sku, skuReduction);

                R skuResult = couponFeignService.saveSkuReduction(skuReduction);
                if (!skuResult.isSuccess()) {
                    log.error("调用远程接口保存 sku 优惠信息出错");
                }
            }
        }
    }

    @Override
    public void saveBaseSpuInfo(SpuInfoEntity spuInfoEntity) {
        this.baseMapper.insert(spuInfoEntity);
    }

    @Override
    public PageUtils queryPageByCondition(Map<String, Object> params) {
        QueryWrapper<SpuInfoEntity> queryWrapper = new QueryWrapper<>();
        String key = (String) params.get("key");
        if (StringUtils.isNotEmpty(key)) {
            queryWrapper.and(wp -> wp.eq("id", key).or().like("spu_name", key));
        }

        String catalogId = (String) params.get("catelogId");
        if (StringUtils.isNoneEmpty(catalogId) && !"0".equals(catalogId)) {
            queryWrapper.eq("catalog_id", catalogId);
        }

        String brandId = (String) params.get("brandId");
        if (StringUtils.isNoneEmpty(brandId) && !"0".equals(brandId)) {
            queryWrapper.eq("brand_id", brandId);
        }

        String status = (String) params.get("status");
        if (StringUtils.isNotEmpty(status)) {
            queryWrapper.eq("publish_status", status);
        }

        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                queryWrapper
        );

        return new PageUtils(page);
    }

}