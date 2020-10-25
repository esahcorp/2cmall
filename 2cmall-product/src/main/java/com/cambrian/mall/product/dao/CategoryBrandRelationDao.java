package com.cambrian.mall.product.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cambrian.mall.product.entity.CategoryBrandRelationEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 品牌分类关联
 * 
 * @author esahcorp
 * @email lostkite@outlook.com
 * @date 2020-09-20 03:15:02
 */
@Mapper
public interface CategoryBrandRelationDao extends BaseMapper<CategoryBrandRelationEntity> {

    void updateBrand(@Param("brandId") Long brandId, @Param("brandName") String brandName);

    void updateCategory(@Param("catId") Long catId, @Param("categoryName") String categoryName);

}
