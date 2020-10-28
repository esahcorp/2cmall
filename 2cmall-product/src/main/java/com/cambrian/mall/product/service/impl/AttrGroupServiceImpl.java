package com.cambrian.mall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cambrian.common.utils.PageUtils;
import com.cambrian.common.utils.Query;
import com.cambrian.mall.product.dao.AttrAttrgroupRelationDao;
import com.cambrian.mall.product.dao.AttrGroupDao;
import com.cambrian.mall.product.entity.AttrAttrgroupRelationEntity;
import com.cambrian.mall.product.entity.AttrGroupEntity;
import com.cambrian.mall.product.service.AttrGroupService;
import com.cambrian.mall.product.vo.AttrRelationVO;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service("attrGroupService")
public class AttrGroupServiceImpl extends ServiceImpl<AttrGroupDao, AttrGroupEntity> implements AttrGroupService {

    private AttrAttrgroupRelationDao relationDao;

    public AttrGroupServiceImpl(AttrAttrgroupRelationDao relationDao) {
        this.relationDao = relationDao;
    }

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrGroupEntity> page = this.page(
                new Query<AttrGroupEntity>().getPage(params),
                new QueryWrapper<AttrGroupEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils queryPage(Long categoryId, Map<String, Object> params) {
        String key = (String) params.get("key");
        QueryWrapper<AttrGroupEntity> wrapper = new QueryWrapper<>();
        if (categoryId != 0) {
            wrapper.eq("catelog_id", categoryId);
        }
        if (StringUtils.isNotEmpty(key)) {
            // select * from pms_attr_group where attr_group_id = ? or attr_group_name like ?
            wrapper.and(wrp -> wrp.eq("attr_group_id", key).or().like("attr_group_name", key));
        }
        IPage<AttrGroupEntity> page = this.page(
                new Query<AttrGroupEntity>().getPage(params),
                wrapper
        );

        return new PageUtils(page);
    }

    @Override
    public void removeRelationBatch(List<AttrRelationVO> relations) {
        List<AttrAttrgroupRelationEntity> entities = relations.stream().map(vo -> {
            AttrAttrgroupRelationEntity entity = new AttrAttrgroupRelationEntity();
            entity.setAttrId(vo.getAttrId());
            entity.setAttrGroupId(vo.getAttrGroupId());
            return entity;
        }).collect(Collectors.toList());
        relationDao.deleteBatch(entities);
    }

}