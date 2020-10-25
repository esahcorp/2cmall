package com.cambrian.mall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cambrian.common.utils.PageUtils;
import com.cambrian.common.utils.Query;
import com.cambrian.mall.product.dao.BrandDao;
import com.cambrian.mall.product.dao.CategoryBrandRelationDao;
import com.cambrian.mall.product.dao.CategoryDao;
import com.cambrian.mall.product.entity.BrandEntity;
import com.cambrian.mall.product.entity.CategoryBrandRelationEntity;
import com.cambrian.mall.product.entity.CategoryEntity;
import com.cambrian.mall.product.service.CategoryBrandRelationService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;


@Service("categoryBrandRelationService")
public class CategoryBrandRelationServiceImpl extends ServiceImpl<CategoryBrandRelationDao, CategoryBrandRelationEntity> implements CategoryBrandRelationService {

    @Resource
    private CategoryDao categoryDao;

    @Resource
    private BrandDao brandDao;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryBrandRelationEntity> page = this.page(
                new Query<CategoryBrandRelationEntity>().getPage(params),
                new QueryWrapper<CategoryBrandRelationEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void saveDetail(CategoryBrandRelationEntity categoryBrandRelation) {
        Long brandId = categoryBrandRelation.getBrandId();
        Long categoryId = categoryBrandRelation.getCatelogId();
        BrandEntity brandEntity = brandDao.selectById(brandId);
        CategoryEntity categoryEntity = categoryDao.selectById(categoryId);
        categoryBrandRelation.setBrandName(brandEntity.getName());
        categoryBrandRelation.setCatelogName(categoryEntity.getName());
        this.save(categoryBrandRelation);

    }

    @Override
    public void updateBrand(Long brandId, String brandName) {
        this.baseMapper.updateBrand(brandId, brandName);
    }

    @Override
    public void updateCategory(Long catId, String categoryName) {
        this.baseMapper.updateCategory(catId, categoryName);
    }

}