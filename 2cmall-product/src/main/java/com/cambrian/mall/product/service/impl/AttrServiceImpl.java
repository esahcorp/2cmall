package com.cambrian.mall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cambrian.common.constant.ProductConstants.AttrTypeEnum;
import com.cambrian.common.utils.PageUtils;
import com.cambrian.common.utils.Query;
import com.cambrian.mall.product.dao.AttrAttrgroupRelationDao;
import com.cambrian.mall.product.dao.AttrDao;
import com.cambrian.mall.product.dao.AttrGroupDao;
import com.cambrian.mall.product.dao.CategoryDao;
import com.cambrian.mall.product.entity.AttrAttrgroupRelationEntity;
import com.cambrian.mall.product.entity.AttrEntity;
import com.cambrian.mall.product.entity.AttrGroupEntity;
import com.cambrian.mall.product.entity.CategoryEntity;
import com.cambrian.mall.product.service.AttrService;
import com.cambrian.mall.product.service.CategoryService;
import com.cambrian.mall.product.vo.AttrRespVO;
import com.cambrian.mall.product.vo.AttrVO;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service("attrService")
public class AttrServiceImpl extends ServiceImpl<AttrDao, AttrEntity> implements AttrService {

    private AttrAttrgroupRelationDao relationDao;
    private CategoryDao categoryDao;
    private AttrGroupDao attrGroupDao;

    private CategoryService categoryService;

    public AttrServiceImpl(AttrAttrgroupRelationDao relationDao, CategoryDao categoryDao,
                           AttrGroupDao attrGroupDao, CategoryService categoryService) {
        this.relationDao = relationDao;
        this.categoryDao = categoryDao;
        this.attrGroupDao = attrGroupDao;
        this.categoryService = categoryService;
    }

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrEntity> page = this.page(
                new Query<AttrEntity>().getPage(params),
                new QueryWrapper<>()
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils queryPageByCategory(String attrType, Long categoryId, Map<String, Object> params) {
        String key = (String) params.get("key");
        QueryWrapper<AttrEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("attr_type", AttrTypeEnum.messageOf(attrType).ordinal());
        if (categoryId != 0) {
            wrapper.eq("catelog_id", categoryId);
        }
        if (StringUtils.isNotEmpty(key)) {
            wrapper.and(wrp -> wrp.eq("attr_id", key).or().like("attr_name", key));
        }
        IPage<AttrEntity> page = this.page(
                new Query<AttrEntity>().getPage(params),
                wrapper
        );

        List<AttrEntity> records = page.getRecords();
        List<AttrRespVO> list = records.stream().map(attrEntity -> {
            AttrRespVO respVO = new AttrRespVO();
            BeanUtils.copyProperties(attrEntity, respVO);
            if (AttrTypeEnum.isBase(attrType)) {
                AttrAttrgroupRelationEntity relationEntity = relationDao.selectOne(
                        new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", attrEntity.getAttrId())
                );
                if (relationEntity != null && relationEntity.getAttrGroupId() != null) {
                    AttrGroupEntity attrGroupEntity = attrGroupDao.selectById(relationEntity.getAttrGroupId());
                    respVO.setGroupName(attrGroupEntity.getAttrGroupName());
                }
            }
            CategoryEntity categoryEntity = categoryDao.selectById(attrEntity.getCatelogId());
            respVO.setCatelogName(categoryEntity.getName());
            return respVO;
        }).collect(Collectors.toList());
        PageUtils pageUtils = new PageUtils(page);
        pageUtils.setList(list);
        return pageUtils;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void saveAttr(AttrVO attr) {
        AttrEntity attrEntity = new AttrEntity();
        BeanUtils.copyProperties(attr, attrEntity);
        this.save(attrEntity);
        if (AttrTypeEnum.isBase(attr.getAttrType()) && attr.getAttrGroupId() != null) {
            AttrAttrgroupRelationEntity relationEntity = new AttrAttrgroupRelationEntity();
            relationEntity.setAttrGroupId(attr.getAttrGroupId());
            relationEntity.setAttrId(attrEntity.getAttrId());
            relationDao.insert(relationEntity);
        }
    }

    @Override
    public AttrRespVO getAttrInfo(Long attrId) {
        AttrEntity attrEntity = this.getById(attrId);
        AttrRespVO respVO = new AttrRespVO();
        BeanUtils.copyProperties(attrEntity, respVO);

        if (AttrTypeEnum.isBase(attrEntity.getAttrType())) {
            AttrAttrgroupRelationEntity relationEntity = relationDao.selectOne(
                    new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", attrId)
            );
            if (relationEntity != null) {
                respVO.setAttrGroupId(relationEntity.getAttrGroupId());
                AttrGroupEntity attrGroupEntity = attrGroupDao.selectById(relationEntity.getAttrGroupId());
                if (attrGroupEntity != null) {
                    respVO.setAttrName(attrGroupEntity.getAttrGroupName());
                }
            }
        }

        Long[] categoryPath = categoryService.findCategoryPath(attrEntity.getCatelogId());
        respVO.setCatelogPath(categoryPath);
        CategoryEntity categoryEntity = categoryDao.selectById(attrEntity.getCatelogId());
        if (categoryEntity != null) {
            respVO.setCatelogName(categoryEntity.getName());
        }

        return respVO;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateAttr(AttrVO attr) {
        AttrEntity attrEntity = new AttrEntity();
        BeanUtils.copyProperties(attr, attrEntity);
        this.updateById(attrEntity);

        if (attr.getAttrGroupId() != null && AttrTypeEnum.isBase(attr.getAttrType())) {
            AttrAttrgroupRelationEntity relationEntity = new AttrAttrgroupRelationEntity();
            relationEntity.setAttrId(attr.getAttrId());
            relationEntity.setAttrGroupId(attr.getAttrGroupId());
            Integer c = relationDao.selectCount(
                    new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", attr.getAttrId())
            );
            if (c > 0) {
                relationDao.update(relationEntity,
                        new UpdateWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", attr.getAttrId()));
            } else {
                relationDao.insert(relationEntity);
            }
        }
    }

    @Override
    public List<AttrEntity> listRelationAttrByGroup(Long attrGroupId) {
        List<AttrAttrgroupRelationEntity> relations =
                relationDao.selectList(
                        new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_group_id", attrGroupId)
                );
        if (CollectionUtils.isEmpty(relations)) {
            return Collections.emptyList();
        }
        List<Long> attrIds = relations.stream().map(AttrAttrgroupRelationEntity::getAttrId).collect(Collectors.toList());
        return this.listByIds(attrIds);
    }

    @Override
    public PageUtils listNotRelatedAttrViaCategory(Long attrGroupId, Map<String, Object> params) {
        // 1. 查出当前分组所属的分类
        AttrGroupEntity attrGroupEntity = attrGroupDao.selectById(attrGroupId);
        Long catelogId = attrGroupEntity.getCatelogId();

        // 2. 查出当前分类下的所有分组已经关联的属性
        List<AttrGroupEntity> attrGroupEntities =
                attrGroupDao.selectList(new QueryWrapper<AttrGroupEntity>().eq("catelog_id", catelogId));
        List<Long> groupIds =
                attrGroupEntities.stream().map(AttrGroupEntity::getAttrGroupId).collect(Collectors.toList());

        List<AttrAttrgroupRelationEntity> relatedRelations =
                relationDao.selectList(
                        // 无须判断非空，因为至少包含自身
                        new QueryWrapper<AttrAttrgroupRelationEntity>().in("attr_group_id", groupIds)
                );
        List<Long> relatedAttrIds =
                relatedRelations.stream().map(AttrAttrgroupRelationEntity::getAttrId).collect(Collectors.toList());

        // 3. 查出排除 step2 的结果后所剩的当前分类下的属性
        QueryWrapper<AttrEntity> wrapper = new QueryWrapper<>();

        String key = (String) params.get("key");
        if (StringUtils.isNotEmpty(key)) {
            wrapper.and(wrp -> wrp.eq("attr_id", key).or().like("attr_name", key));
        }
        if (!CollectionUtils.isEmpty(relatedRelations)) {
            wrapper.notIn("attr_id", relatedAttrIds);
        }
        wrapper.eq("catelog_id", catelogId)
                // 限定基本属性
                .eq("attr_type", AttrTypeEnum.ATTR_TYPE_BASE.ordinal());
        IPage<AttrEntity> page = this.baseMapper.selectPage(
                new Query<AttrEntity>().getPage(params),
                wrapper);
        return new PageUtils(page);

    }

}