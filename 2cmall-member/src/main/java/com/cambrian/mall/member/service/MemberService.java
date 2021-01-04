package com.cambrian.mall.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cambrian.common.utils.PageUtils;
import com.cambrian.mall.member.entity.MemberEntity;
import com.cambrian.mall.member.exception.PhoneExistException;
import com.cambrian.mall.member.exception.UsernameExistException;
import com.cambrian.mall.member.vo.MemberUserRegisterVO;

import java.util.Map;

/**
 * 会员
 *
 * @author esahcorp
 * @date 2020-09-29 17:23:15
 */
public interface MemberService extends IService<MemberEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void register(MemberUserRegisterVO registerVO);

    void checkUsernameUnique(String username) throws UsernameExistException;

    void checkPhoneUnique(String phone) throws PhoneExistException;
}

