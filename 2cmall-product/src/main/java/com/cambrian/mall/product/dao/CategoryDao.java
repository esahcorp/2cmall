package com.cambrian.mall.product.dao;

import com.cambrian.mall.product.entity.CategoryEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品三级分类
 * 
 * @author esahcorp
 * @email lostkite@outlook.com
 * @date 2020-09-20 03:15:02
 */
@Mapper
public interface CategoryDao extends BaseMapper<CategoryEntity> {
	
}
