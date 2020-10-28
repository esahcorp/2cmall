package com.cambrian.mall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cambrian.common.utils.PageUtils;
import com.cambrian.mall.product.entity.AttrEntity;
import com.cambrian.mall.product.vo.AttrRespVO;
import com.cambrian.mall.product.vo.AttrVO;

import java.util.List;
import java.util.Map;

/**
 * 商品属性
 *
 * @author esahcorp
 * @email lostkite@outlook.com
 * @date 2020-09-20 03:15:02
 */
public interface AttrService extends IService<AttrEntity> {

    PageUtils queryPage(Map<String, Object> params);

    PageUtils queryPageByCategory(String attrType, Long categoryId, Map<String, Object> params);

    void saveAttr(AttrVO attr);

    AttrRespVO getAttrInfo(Long attrId);

    void updateAttr(AttrVO attr);

    List<AttrEntity> listRelationAttrByGroup(Long attrGroupId);

    /**
     * 获取当前分组没有关联的同分类属性
     *
     * @param attrGroupId 属性分组 id
     * @param params 参数
     * @return 分页列表
     */
    PageUtils listNotRelatedAttrViaCategory(Long attrGroupId, Map<String, Object> params);
}

