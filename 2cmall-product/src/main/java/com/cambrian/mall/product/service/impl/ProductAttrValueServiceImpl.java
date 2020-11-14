package com.cambrian.mall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cambrian.common.utils.PageUtils;
import com.cambrian.common.utils.Query;
import com.cambrian.mall.product.dao.ProductAttrValueDao;
import com.cambrian.mall.product.entity.ProductAttrValueEntity;
import com.cambrian.mall.product.service.ProductAttrValueService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service("productAttrValueService")
public class ProductAttrValueServiceImpl extends ServiceImpl<ProductAttrValueDao, ProductAttrValueEntity> implements ProductAttrValueService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<ProductAttrValueEntity> page = this.page(
                new Query<ProductAttrValueEntity>().getPage(params),
                new QueryWrapper<ProductAttrValueEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<ProductAttrValueEntity> listBySpuId(Long spuId) {
        return this.list(new QueryWrapper<ProductAttrValueEntity>().eq("spu_id", spuId));
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateValueViaSpu(Long spuId, List<ProductAttrValueEntity> values) {
        //
        // 1. 删除已关联的商品属性值
        // ------------------------------------------------------------------------------
        this.remove(new QueryWrapper<ProductAttrValueEntity>().eq("spu_id", spuId));
        //
        // 2. 保存新的属性值
        // ------------------------------------------------------------------------------
        List<ProductAttrValueEntity> entities = values.stream().map(ve -> {
            ve.setSpuId(spuId);
            return ve;
        }).collect(Collectors.toList());
        this.saveBatch(entities);
    }

}