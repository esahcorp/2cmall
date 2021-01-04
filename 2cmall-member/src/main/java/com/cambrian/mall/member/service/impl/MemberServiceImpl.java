package com.cambrian.mall.member.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cambrian.common.utils.PageUtils;
import com.cambrian.common.utils.Query;
import com.cambrian.mall.member.dao.MemberDao;
import com.cambrian.mall.member.dao.MemberLevelDao;
import com.cambrian.mall.member.entity.MemberEntity;
import com.cambrian.mall.member.exception.UsernameExistException;
import com.cambrian.mall.member.exception.PhoneExistException;
import com.cambrian.mall.member.service.MemberService;
import com.cambrian.mall.member.vo.MemberUserRegisterVO;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;


@Service("memberService")
public class MemberServiceImpl extends ServiceImpl<MemberDao, MemberEntity> implements MemberService {

    private final MemberLevelDao memberLevelDao;

    public MemberServiceImpl(MemberLevelDao memberLevelDao) {
        this.memberLevelDao = memberLevelDao;
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
    public void register(MemberUserRegisterVO registerVO) {
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
    public void checkUsernameUnique(String username) {
        Integer exist = this.baseMapper.checkUsernameUnique(username);
        if (null != exist) {
            throw new UsernameExistException();
        }
    }
}