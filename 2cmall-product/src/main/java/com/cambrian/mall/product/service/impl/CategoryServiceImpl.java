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
import com.cambrian.mall.product.vo.Catalog2VO;
import com.cambrian.mall.product.vo.Catalog2VO.Catalog3VO;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {

    private final CategoryBrandRelationService categoryBrandRelationService;

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

    @Override
    public List<CategoryEntity> listRootCategories() {
        return listCategoriesByParentId(0L);
    }

    @Override
    public Map<String, List<Catalog2VO>> listCatalogJsonModel() {
        List<CategoryEntity> categoryEntities = this.listRootCategories();
        // 最外层 string:list 的映射
        Map<String, List<Catalog2VO>> catalogJsonModel = null;
        if (!CollectionUtils.isEmpty(categoryEntities)) {
            catalogJsonModel = categoryEntities.stream()
                    .map(CategoryEntity::getCatId)
                    .collect(Collectors.toMap(Object::toString, l1Id -> {
                        //
                        // 构建 c2 list
                        // ------------------------------------------------------------------------------
                        List<CategoryEntity> category2Entities = listCategoriesByParentId(l1Id);
                        List<Catalog2VO> catalog2Vos = new ArrayList<>();
                        if (!CollectionUtils.isEmpty(category2Entities)) {
                            catalog2Vos = category2Entities.stream()
                                    .map(c2 -> {
                                        //===============================================================================
                                        //  构建 c2 VO
                                        //===============================================================================
                                        Long l2Id = c2.getCatId();
                                        //
                                        // 1. 依据 id 查询 c2 对应的 c3 list
                                        // ------------------------------------------------------------------------------
                                        List<CategoryEntity> category3Entities = listCategoriesByParentId(l2Id);
                                        List<Catalog3VO> catalog3Vos = null;
                                        if (!CollectionUtils.isEmpty(category3Entities)) {
                                            catalog3Vos = category3Entities.stream()
                                                    .map(c3 -> new Catalog3VO(
                                                            l2Id.toString(), c3.getCatId().toString(), c3.getName()
                                                    ))
                                                    .collect(Collectors.toList());
                                        }
                                        //
                                        // 2. 组装 c2 VO
                                        // ------------------------------------------------------------------------------
                                        return new Catalog2VO(l1Id.toString(), catalog3Vos, l2Id.toString(), c2.getName());
                                    })
                                    .collect(Collectors.toList());
                        }
                        return catalog2Vos;
                    }));
        }
        return catalogJsonModel;
    }

    private List<CategoryEntity> listCategoriesByParentId(Long parentId) {
        return this.list(new QueryWrapper<CategoryEntity>().eq("parent_cid", parentId));
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