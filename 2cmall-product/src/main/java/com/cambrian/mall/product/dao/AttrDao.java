package com.cambrian.mall.product.dao;

import com.cambrian.mall.product.entity.AttrEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 商品属性
 * 
 * @author esahcorp
 * @email lostkite@outlook.com
 * @date 2020-09-20 03:15:02
 */
@Mapper
public interface AttrDao extends BaseMapper<AttrEntity> {

    /**
     * 从给出的属性 id 集合中过滤出数据库中 searchType=1 的记录并返回其 id
     *
     * @param baseAttrIds list
     * @return list
     */
    List<Long> selectSearchAttrIdsInIds(@Param("baseAttrIds") List<Long> baseAttrIds);
}
