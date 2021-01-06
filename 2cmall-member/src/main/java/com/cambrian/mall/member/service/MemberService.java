package com.cambrian.mall.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cambrian.common.utils.PageUtils;
import com.cambrian.mall.member.entity.MemberEntity;
import com.cambrian.mall.member.exception.PhoneExistException;
import com.cambrian.mall.member.exception.UsernameExistException;
import com.cambrian.mall.member.to.MemberUserRegisterDTO;
import com.cambrian.mall.member.to.MemberUserSigninDTO;
import com.cambrian.mall.member.to.WeiboAccessTokenDTO;

import javax.security.auth.login.AccountNotFoundException;
import javax.security.auth.login.FailedLoginException;
import java.util.Map;

/**
 * 会员
 *
 * @author esahcorp
 * @date 2020-09-29 17:23:15
 */
public interface MemberService extends IService<MemberEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void register(MemberUserRegisterDTO registerVO);

    void checkUsernameUnique(String username) throws UsernameExistException;

    void checkPhoneUnique(String phone) throws PhoneExistException;

    MemberEntity signin(MemberUserSigninDTO vo) throws AccountNotFoundException, FailedLoginException;

    MemberEntity accessSocialUser(WeiboAccessTokenDTO accessToken);

    MemberEntity createSocialUser(WeiboAccessTokenDTO accessToken);
}

