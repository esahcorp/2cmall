package com.cambrian.mall.product.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cambrian.common.constant.ProductConstants;
import com.cambrian.common.utils.PageUtils;
import com.cambrian.common.utils.Query;
import com.cambrian.mall.product.dao.CategoryDao;
import com.cambrian.mall.product.entity.CategoryEntity;
import com.cambrian.mall.product.service.CategoryBrandRelationService;
import com.cambrian.mall.product.service.CategoryService;
import com.cambrian.mall.product.vo.Catalog2VO;
import com.cambrian.mall.product.vo.Catalog2VO.Catalog3VO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


@Slf4j
@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {

    private final CategoryBrandRelationService categoryBrandRelationService;
    private final StringRedisTemplate stringRedisTemplate;

    public CategoryServiceImpl(CategoryBrandRelationService categoryBrandRelationService,
                               StringRedisTemplate stringRedisTemplate) {
        this.categoryBrandRelationService = categoryBrandRelationService;
        this.stringRedisTemplate = stringRedisTemplate;
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
        return this.list(new QueryWrapper<CategoryEntity>().eq("parent_cid", 0L));
    }

    @Override
    public Map<String, List<Catalog2VO>> listCatalogJsonModel() {

        //===============================================================================
        //  优化：
        //      1. 缓存空结果——缓存穿透
        //      2. 设置缓存过期时间（加额外随机值）——缓存雪崩
        //      3. 查询缓存时加锁——解决缓存击穿
        //===============================================================================

        Map<String, List<Catalog2VO>> jsonModel = listCatalogJsonModelFromCache();
        if (jsonModel != null) {
            return jsonModel;
        }
        /* 处理缓存为空的情况 */
        return getCatalogJsonFromDbWithRedisLock();
    }

    @SuppressWarnings("unused")
    private synchronized Map<String, List<Catalog2VO>> getCatalogJsonFromDbWithLocalLock() {
        return getCatalogJsonFromDb();
    }

    private synchronized Map<String, List<Catalog2VO>> getCatalogJsonFromDbWithRedisLock() {
        String uuid = UUID.randomUUID().toString();
        // SET NX EX 加锁
        Boolean locked = stringRedisTemplate.opsForValue()
                .setIfAbsent(ProductConstants.RedisLockKey.LOCK_CATALOG_JSON, uuid, 300, TimeUnit.SECONDS);
        if (Boolean.TRUE.equals(locked)) {
            Map<String, List<Catalog2VO>> jsonModel = getCatalogJsonFromDb();
            // 释放锁
            String releaseLockScript =
                    "if redis.call(\"get\",KEYS[1]) == ARGV[1]\n" +
                    "then\n" +
                    "    return redis.call(\"del\",KEYS[1])\n" +
                    "else\n" +
                    "    return 0\n" +
                    "end";
            stringRedisTemplate.execute(
                    new DefaultRedisScript<>(releaseLockScript, Long.class),
                    Collections.singletonList(ProductConstants.RedisLockKey.LOCK_CATALOG_JSON),
                    uuid);
            return jsonModel;
        } else {
            /* 等待 300ms 后自旋加锁 */
            try {
                TimeUnit.MILLISECONDS.sleep(300);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            return getCatalogJsonFromDbWithRedisLock();
        }
    }

    private Map<String, List<Catalog2VO>> getCatalogJsonFromDb() {
        /* 获取锁之后再次检查缓存，因为可能出现在等待锁期间前序请求已经重新设置了缓存的情况 */
        Map<String, List<Catalog2VO>> jsonModel = listCatalogJsonModelFromCache();
        if (jsonModel != null) {
            return jsonModel;
        }
        jsonModel = listCatalogJsonModelFromDb();
        if (jsonModel == null) {
            // 存储空值避免缓存穿透
            stringRedisTemplate.opsForValue()
                    .set(ProductConstants.CacheKey.CATALOG_JSON, "", 5, TimeUnit.MINUTES);
        } else {
            // 重新设置缓存，为了避免缓存雪崩需要设置额外的失效时间增幅
            // 获取 1- 5分钟的随机增值
            long randomIncrease = Math.round(1 * Math.random() * (5 - 1));
            long expireMinutes = 24 * 60 + randomIncrease;
            stringRedisTemplate.opsForValue()
                    .set(ProductConstants.CacheKey.CATALOG_JSON, JSON.toJSONString(jsonModel), expireMinutes, TimeUnit.MINUTES);
        }
        return jsonModel;
    }

    private Map<String, List<Catalog2VO>> listCatalogJsonModelFromCache() {
        String catalogJsonString = stringRedisTemplate.opsForValue().get(ProductConstants.CacheKey.CATALOG_JSON);
        if (StringUtils.isEmpty(catalogJsonString)) {
            return null;
        }
        return JSON.parseObject(catalogJsonString, new TypeReference<Map<String, List<Catalog2VO>>>() {
        });
    }

    private Map<String, List<Catalog2VO>> listCatalogJsonModelFromDb() {
        /*
         * 优化一 查询一次数据库，降低 IO 频率，提升性能
         */
        List<CategoryEntity> allCategories = this.baseMapper.selectList(null);

        List<CategoryEntity> categoryEntities = this.filterCategoriesByParentId(allCategories, 0L);
        // 最外层 string:list 的映射
        Map<String, List<Catalog2VO>> catalogJsonModel = null;
        if (!CollectionUtils.isEmpty(categoryEntities)) {
            catalogJsonModel = categoryEntities.stream()
                    .map(CategoryEntity::getCatId)
                    .collect(Collectors.toMap(Object::toString, l1Id -> {
                        //
                        // 构建 c2 list
                        // ------------------------------------------------------------------------------
                        List<CategoryEntity> category2Entities = filterCategoriesByParentId(allCategories, l1Id);
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
                                        List<CategoryEntity> category3Entities = filterCategoriesByParentId(allCategories, l2Id);
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
        log.debug("线程{}从数据库获取了三级类别数据", Thread.currentThread().getId());
        return catalogJsonModel;
    }

    private List<CategoryEntity> filterCategoriesByParentId(List<CategoryEntity> allCategories, Long parentId) {
        return allCategories.stream().filter(c -> c.getParentCid().equals(parentId)).collect(Collectors.toList());
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