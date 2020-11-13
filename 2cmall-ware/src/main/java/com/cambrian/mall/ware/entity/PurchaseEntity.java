package com.cambrian.mall.ware.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.cambrian.common.constant.WareConstants;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 采购信息
 *
 * @author esahcorp
 * @email lostkite@outlook.com
 * @date 2020-09-29 18:29:12
 */
@Data
@TableName("wms_purchase")
public class PurchaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 判断当前采购单状态能否被领取
     *
     * @return can received if true
     */
    public boolean canReceive() {
        return WareConstants.PurchaseStatusEnum.CREATED.getCode() == this.getStatus() ||
                WareConstants.PurchaseStatusEnum.ASSIGNED.getCode() == this.getStatus();
    }

    /**
     * 采购单id
     */
    @TableId
    private Long id;
    /**
     * 采购人id
     */
    private Long assigneeId;
    /**
     * 采购人名
     */
    private String assigneeName;
    /**
     * 联系方式
     */
    private String phone;
    /**
     * 优先级
     */
    private Integer priority;
    /**
     * 状态
     */
    private Integer status;
    /**
     * 仓库id
     */
    private Long wareId;
    /**
     * 总金额
     */
    private BigDecimal amount;
    /**
     * 创建日期
     */
    private Date createTime;
    /**
     * 更新日期
     */
    private Date updateTime;

}
