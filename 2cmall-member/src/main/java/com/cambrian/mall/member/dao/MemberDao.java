package com.cambrian.mall.member.dao;

import com.cambrian.mall.member.entity.MemberEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 会员
 * 
 * @author esahcorp
 * @email lostkite@outlook.com
 * @date 2020-09-29 17:23:15
 */
@Mapper
public interface MemberDao extends BaseMapper<MemberEntity> {
	
}
