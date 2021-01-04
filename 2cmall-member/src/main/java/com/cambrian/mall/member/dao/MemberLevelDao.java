package com.cambrian.mall.member.dao;

import com.cambrian.mall.member.entity.MemberLevelEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 会员等级
 * 
 * @author esahcorp
 * @date 2020-09-29 17:23:15
 */
@Mapper
public interface MemberLevelDao extends BaseMapper<MemberLevelEntity> {

    long getDefaultLevelId();
}
