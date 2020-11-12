package com.cambrian.mall.product.vo;

import com.cambrian.mall.product.entity.AttrEntity;
import lombok.Data;

import java.util.List;

/**
 * @author kuma 2020-10-29
 */
@Data
public class AttrGroupWithAttrVO {

    /**
     * 分组id
     */
    private Long attrGroupId;
    /**
     * 组名
     */
    private String attrGroupName;
    /**
     * 排序
     */
    private Integer sort;
    /**
     * 描述
     */
    private String descript;
    /**
     * 组图标
     */
    private String icon;
    /**
     * 所属分类id
     */
    private Long catelogId;

    private List<AttrEntity> attrs;
}
