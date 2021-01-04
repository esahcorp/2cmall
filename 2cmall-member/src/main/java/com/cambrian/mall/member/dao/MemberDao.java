package com.cambrian.mall.member.dao;

import com.cambrian.mall.member.entity.MemberEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 会员
 * 
 * @author esahcorp
 * @date 2020-09-29 17:23:15
 */
@Mapper
public interface MemberDao extends BaseMapper<MemberEntity> {

    int checkPhoneUnique(@Param("phone") String phone);

    int checkUsernameUnique(@Param("username") String username);
}
