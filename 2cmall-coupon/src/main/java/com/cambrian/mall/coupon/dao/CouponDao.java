package com.cambrian.mall.coupon.dao;

import com.cambrian.mall.coupon.entity.CouponEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 优惠券信息
 * 
 * @author esahcorp
 * @email lostkite@outlook.com
 * @date 2020-09-29 17:07:22
 */
@Mapper
public interface CouponDao extends BaseMapper<CouponEntity> {
	
}
