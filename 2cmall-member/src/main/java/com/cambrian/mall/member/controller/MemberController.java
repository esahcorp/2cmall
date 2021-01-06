package com.cambrian.mall.member.controller;

import com.cambrian.common.utils.PageUtils;
import com.cambrian.common.utils.R;
import com.cambrian.mall.member.entity.MemberEntity;
import com.cambrian.mall.member.exception.PhoneExistException;
import com.cambrian.mall.member.exception.UsernameExistException;
import com.cambrian.mall.member.service.MemberService;
import com.cambrian.mall.member.to.MemberUserRegisterDTO;
import com.cambrian.mall.member.to.MemberUserSigninDTO;
import com.cambrian.mall.member.to.WeiboAccessTokenDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.security.auth.login.AccountNotFoundException;
import javax.security.auth.login.FailedLoginException;
import java.util.Arrays;
import java.util.Map;



/**
 * 会员
 *
 * @author esahcorp
 * @date 2020-09-29 17:23:15
 */
@RestController
@RequestMapping("member/member")
public class MemberController {

    @Autowired
    private MemberService memberService;

    @PostMapping("/register")
    public R register(@RequestBody MemberUserRegisterDTO registerVO) {
        try {
            memberService.register(registerVO);
        } catch (UsernameExistException e) {
            return R.error("用户名已存在");
        } catch (PhoneExistException e) {
            return R.error("手机号已注册");
        }
        return R.ok();
    }

    @PostMapping("/signin")
    public R signin(@RequestBody MemberUserSigninDTO vo) {
        try {
            MemberEntity signin = memberService.signin(vo);
            return R.ok().put("user", signin);
        } catch (AccountNotFoundException | FailedLoginException e) {
            return R.error("用户名或密码错误");
        }
    }

    @PostMapping("/oauth2/signin")
    public R oauth2Signin(@RequestBody WeiboAccessTokenDTO accessToken) {
        try {
            MemberEntity memberEntity = memberService.accessSocialUser(accessToken);
            return R.ok().put("social_user", memberEntity);
        } catch (Exception e) {
            return R.error("获取社交账户失败");
        }
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    // @RequiresPermissions("member:member:list")
    public R list(@RequestParam Map<String, Object> params) {

        PageUtils page = memberService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    // @RequiresPermissions("member:member:info")
    public R info(@PathVariable("id") Long id){
		MemberEntity member = memberService.getById(id);

        return R.ok().put("member", member);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    // @RequiresPermissions("member:member:save")
    public R save(@RequestBody MemberEntity member){
		memberService.save(member);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody MemberEntity member){
		memberService.updateById(member);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    // @RequiresPermissions("member:member:delete")
    public R delete(@RequestBody Long[] ids){
		memberService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
