package com.cambrian.mall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cambrian.common.utils.PageUtils;
import com.cambrian.common.utils.Query;
import com.cambrian.mall.product.dao.CategoryDao;
import com.cambrian.mall.product.entity.CategoryEntity;
import com.cambrian.mall.product.service.CategoryBrandRelationService;
import com.cambrian.mall.product.service.CategoryService;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {

    private CategoryBrandRelationService categoryBrandRelationService;

    public CategoryServiceImpl(CategoryBrandRelationService categoryBrandRelationService) {
        this.categoryBrandRelationService = categoryBrandRelationService;
    }

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryEntity> page = this.page(
                new Query<CategoryEntity>().getPage(params),
                new QueryWrapper<CategoryEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<CategoryEntity> listTree() {
        List<CategoryEntity> all = baseMapper.selectList(null);
        return all.stream().filter(category -> category.getCatLevel() == 1)
                .map(categoryEntity -> {
                    categoryEntity.setChildren(getChildren(categoryEntity, all));
                    return categoryEntity;
                })
                .sorted(Comparator.comparingInt(category -> Optional.of(category.getSort()).orElse(0)))
                .collect(Collectors.toList());
    }

    @Override
    public void removeCategoryByIds(List<Long> ids) {
        // TODO: (kuma, 2020/10/3, [2020/10/3]) 删除前校验
        baseMapper.deleteBatchIds(ids);
    }

    @Override
    public Long[] findCategoryPath(Long categoryId) {
        List<Long> path = new ArrayList<>();
        assemblyPathRecursive(categoryId, path);
        return path.toArray(new Long[0]);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateCascade(CategoryEntity category) {
        this.updateById(category);
        if (StringUtils.isNotEmpty(category.getName())) {
            categoryBrandRelationService.updateCategory(category.getCatId(), category.getName());
        }
    }

    private void assemblyPathRecursive(Long categoryId, List<Long> container) {
        CategoryEntity entity = this.getById(categoryId);
        Long parentCid = entity.getParentCid();
        if (parentCid != 0) {
            assemblyPathRecursive(parentCid, container);
        }
        container.add(categoryId);
    }

    private List<CategoryEntity> getChildren(CategoryEntity root, List<CategoryEntity> all) {
        return all.stream().filter(categoryEntity -> categoryEntity.getParentCid().equals(root.getCatId()))
                .map(categoryEntity -> {
                    categoryEntity.setChildren(getChildren(categoryEntity, all));
                    return categoryEntity;
                })
                .sorted(Comparator.comparingInt(category -> Optional.ofNullable(category.getSort()).orElse(0)))
                .collect(Collectors.toList());
    }

}