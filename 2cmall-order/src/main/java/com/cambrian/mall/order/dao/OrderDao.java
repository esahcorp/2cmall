package com.cambrian.mall.order.dao;

import com.cambrian.mall.order.entity.OrderEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单
 * 
 * @author esahcorp
 * @email lostkite@outlook.com
 * @date 2020-09-29 18:22:35
 */
@Mapper
public interface OrderDao extends BaseMapper<OrderEntity> {
	
}
