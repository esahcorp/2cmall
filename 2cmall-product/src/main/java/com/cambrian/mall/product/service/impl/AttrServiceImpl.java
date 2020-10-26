package com.cambrian.mall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
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
    public PageUtils queryPageByCategory(Long categoryId, Map<String, Object> params) {
        String key = (String) params.get("key");
        QueryWrapper<AttrEntity> wrapper = new QueryWrapper<>();
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
            AttrAttrgroupRelationEntity relationEntity = relationDao.selectOne(
                    new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", attrEntity.getAttrId())
            );
            if (relationEntity != null) {
                AttrGroupEntity attrGroupEntity = attrGroupDao.selectById(relationEntity.getAttrGroupId());
                respVO.setGroupName(attrGroupEntity.getAttrGroupName());
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
        AttrAttrgroupRelationEntity relationEntity = new AttrAttrgroupRelationEntity();
        relationEntity.setAttrGroupId(attr.getAttrGroupId());
        relationEntity.setAttrId(attr.getAttrId());
        relationDao.insert(relationEntity);
    }

    @Override
    public AttrRespVO getAttrInfo(Long attrId) {
        AttrEntity attrEntity = this.getById(attrId);
        AttrRespVO respVO = new AttrRespVO();
        BeanUtils.copyProperties(attrEntity, respVO);
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

        if (attr.getAttrGroupId() != null) {
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

}