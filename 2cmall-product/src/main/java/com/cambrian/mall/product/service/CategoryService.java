package com.cambrian.mall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cambrian.common.utils.PageUtils;
import com.cambrian.mall.product.entity.CategoryEntity;

import java.util.List;
import java.util.Map;

/**
 * 商品三级分类
 *
 * @author esahcorp
 * @email lostkite@outlook.com
 * @date 2020-09-20 03:15:02
 */
public interface CategoryService extends IService<CategoryEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 树形结构类别
     *
     * @return list with tree structure
     */
    List<CategoryEntity> listTree();
}

