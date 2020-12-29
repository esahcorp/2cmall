package com.cambrian.mall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cambrian.common.utils.PageUtils;
import com.cambrian.mall.product.entity.AttrGroupEntity;
import com.cambrian.mall.product.vo.AttrGroupWithAttrVO;
import com.cambrian.mall.product.vo.AttrRelationVO;
import com.cambrian.mall.product.vo.SpuItemAttrGroupVO;

import java.util.List;
import java.util.Map;

/**
 * 属性分组
 *
 * @author esahcorp
 * @email lostkite@outlook.com
 * @date 2020-09-20 03:15:02
 */
public interface AttrGroupService extends IService<AttrGroupEntity> {

    PageUtils queryPage(Map<String, Object> params);

    PageUtils queryPage(Long categoryId, Map<String, Object> params);

    void removeRelationBatch(List<AttrRelationVO> relations);

    List<AttrGroupWithAttrVO> listGroupWithAttrByCatalogId(Long catalogId);

    List<SpuItemAttrGroupVO> listGroupWithAttrValue(Long catalogId, Long spuId);
}

