package com.cambrian.mall.product.entity;

import com.baomidou.mybatisplus.annotation.SqlCondition;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.cambrian.common.valid.AddGroup;
import com.cambrian.common.valid.UpdateGroup;
import com.cambrian.common.valid.UpdateStatusGroup;
import com.cambrian.common.valid.ValueSet;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.*;
import java.io.Serializable;

/**
 * 品牌
 *
 * @author esahcorp
 * @email lostkite@outlook.com
 * @date 2020-09-20 03:15:02
 */
@Data
@TableName("pms_brand")
public class BrandEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 品牌id
     */
    @Null(groups = AddGroup.class)
    @NotNull(groups = UpdateGroup.class)
    @TableId
    private Long brandId;
    /**
     * 品牌名
     */
    @NotBlank(message = "品牌名称不能为空", groups = {AddGroup.class, UpdateGroup.class})
    @TableField(condition = SqlCondition.LIKE)
    private String name;
    /**
     * 品牌logo地址
     */
    @URL(message = "品牌logo地址应该是一个URL地址", groups = {AddGroup.class, UpdateGroup.class})
    @NotEmpty(groups = AddGroup.class)
    private String logo;
    /**
     * 介绍
     */
    private String descript;
    /**
     * 显示状态[0-不显示；1-显示]
     */
    @NotNull(groups = {AddGroup.class, UpdateStatusGroup.class})
//	@Range(min = 0, max = 1, groups = {AddGroup.class, UpdateGroup.class})
    @ValueSet(intValues = {0, 1}, groups = {AddGroup.class, UpdateGroup.class, UpdateStatusGroup.class})
    private Integer showStatus;
    /**
     * 检索首字母
     */
    @NotBlank(groups = AddGroup.class)
    @Pattern(regexp = "^[a-zA-Z]$", message = "检索首字母字段应该为一个英文字母", groups = {AddGroup.class, UpdateGroup.class})
    private String firstLetter;
    /**
     * 排序
     */
    @Min(value = 0, groups = {AddGroup.class, UpdateGroup.class})
    @NotNull(groups = AddGroup.class)
    private Integer sort;

}
