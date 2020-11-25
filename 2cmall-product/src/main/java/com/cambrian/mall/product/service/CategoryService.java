package com.cambrian.mall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cambrian.common.utils.PageUtils;
import com.cambrian.mall.product.entity.CategoryEntity;
import com.cambrian.mall.product.vo.Catalog2VO;

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

    /**
     * 批量删除
     * 包含业务逻辑
     *
     * @param ids list of category ids
     */
    void removeCategoryByIds(List<Long> ids);

    Long[] findCategoryPath(Long categoryId);

    void updateCascade(CategoryEntity category);

    List<CategoryEntity> listRootCategories();

    /**
     * 查询三级菜单的映射关系，返回特定的数据结构
     *
     * @return 返回 APP 首页需要的类别 json 结构的数据
     */
    Map<String, List<Catalog2VO>> listCatalogJsonModel();
}

