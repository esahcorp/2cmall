package com.cambrian.mall.member.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cambrian.common.utils.PageUtils;
import com.cambrian.common.utils.Query;
import com.cambrian.mall.member.dao.MemberDao;
import com.cambrian.mall.member.dao.MemberLevelDao;
import com.cambrian.mall.member.entity.MemberEntity;
import com.cambrian.mall.member.exception.PhoneExistException;
import com.cambrian.mall.member.exception.UsernameExistException;
import com.cambrian.mall.member.feign.WeiboFeignService;
import com.cambrian.mall.member.service.MemberService;
import com.cambrian.mall.member.to.MemberUserRegisterDTO;
import com.cambrian.mall.member.to.MemberUserSigninDTO;
import com.cambrian.mall.member.to.WeiboAccessTokenDTO;
import lombok.val;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.security.auth.login.AccountNotFoundException;
import javax.security.auth.login.FailedLoginException;
import java.util.Map;


@Service("memberService")
public class MemberServiceImpl extends ServiceImpl<MemberDao, MemberEntity> implements MemberService {

    private final MemberLevelDao memberLevelDao;
    private final WeiboFeignService weiboClient;

    public MemberServiceImpl(MemberLevelDao memberLevelDao, WeiboFeignService weiboClient) {
        this.memberLevelDao = memberLevelDao;
        this.weiboClient = weiboClient;
    }

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<MemberEntity> page = this.page(
                new Query<MemberEntity>().getPage(params),
                new QueryWrapper<MemberEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void register(MemberUserRegisterDTO registerVO) {
        MemberEntity memberEntity = new MemberEntity();
        // 1. 检查唯一性
        checkUsernameUnique(registerVO.getUsername());
        checkPhoneUnique(registerVO.getPhone());
        memberEntity.setUsername(registerVO.getUsername());
        memberEntity.setMobile(registerVO.getPhone());
        // 2. 密码需转义
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String crypt = passwordEncoder.encode(registerVO.getPassword());
        memberEntity.setPassword(crypt);

        long defaultLevel = memberLevelDao.getDefaultLevelId();
        memberEntity.setLevelId(defaultLevel);
        this.save(memberEntity);
    }

    @Override
    public void checkPhoneUnique(String phone) {
        Integer exist = this.baseMapper.checkPhoneUnique(phone);
        if (null != exist) {
            throw new PhoneExistException();
        }
    }

    @Override
    public MemberEntity signin(MemberUserSigninDTO vo) throws AccountNotFoundException, FailedLoginException {
        MemberEntity entity = this.getOne(
                new QueryWrapper<MemberEntity>().eq("username", vo.getAccount())
                        .or().eq("mobile", vo.getAccount()));
        if (entity == null) {
            throw new AccountNotFoundException();
        }
        String passwordCrypt = entity.getPassword();
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        if (!passwordEncoder.matches(vo.getPassword(), passwordCrypt)) {
            throw new FailedLoginException();
        }
        return entity;
    }

    @Override
    public MemberEntity accessSocialUser(WeiboAccessTokenDTO accessToken) {
        val socialUser = this.getOne(new QueryWrapper<MemberEntity>().eq("social_uid", accessToken.getUid()));
        if (socialUser != null) {
            // 不是第一次登录，更新 token 和 过期时间
            val update = new MemberEntity();
            update.setId(socialUser.getId());
            update.setAccessToken(accessToken.getAccessToken());
            update.setExpiresIn(accessToken.getExpiresIn());
            this.updateById(socialUser);

            socialUser.setAccessToken(accessToken.getAccessToken());
            socialUser.setExpiresIn(accessToken.getExpiresIn());
            return socialUser;
        }
        // 第一次登录系统，注册新的用户信息
        return createSocialUser(accessToken);
    }

    @Override
    public MemberEntity createSocialUser(WeiboAccessTokenDTO accessToken) {
        // 调用 api 获取用户信息
        val token = accessToken.getAccessToken();
        val uid = accessToken.getUid();
        MemberEntity memberEntity;
        try {
            val socialUser = weiboClient.showUser(token, uid);
            memberEntity = socialUser.transToMember();
        } catch (Exception e) {
            memberEntity = new MemberEntity();
        }
        memberEntity.setLevelId(memberLevelDao.getDefaultLevelId());
        memberEntity.setAccessToken(token);
        memberEntity.setSocialUid(uid);
        memberEntity.setExpiresIn(accessToken.getExpiresIn());
        this.save(memberEntity);
        return memberEntity;
    }

    @Override
    public void checkUsernameUnique(String username) {
        Integer exist = this.baseMapper.checkUsernameUnique(username);
        if (null != exist) {
            throw new UsernameExistException();
        }
    }
}