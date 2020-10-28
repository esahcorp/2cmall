package com.cambrian.mall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cambrian.common.utils.PageUtils;
import com.cambrian.mall.product.entity.AttrAttrgroupRelationEntity;
import com.cambrian.mall.product.vo.AttrRelationVO;

import java.util.List;
import java.util.Map;

/**
 * 属性&属性分组关联
 *
 * @author esahcorp
 * @email lostkite@outlook.com
 * @date 2020-09-20 03:15:02
 */
public interface AttrAttrgroupRelationService extends IService<AttrAttrgroupRelationEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveRelations(List<AttrRelationVO> vos);
}

