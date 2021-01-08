package com.cambrian.mall.auth.controller;

import com.alibaba.fastjson.TypeReference;
import com.cambrian.common.constant.AuthServerConstants;
import com.cambrian.common.to.MemberEntityDTO;
import com.cambrian.common.utils.R;
import com.cambrian.mall.auth.feign.MemberFeignService;
import com.cambrian.mall.auth.vo.UserSigninVO;
import lombok.val;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;

/**
 * @author kuma 2021-01-04
 */
@Controller
public class SigninController {

    private final MemberFeignService memberFeignService;

    public SigninController(MemberFeignService memberFeignService) {
        this.memberFeignService = memberFeignService;
    }

    @PostMapping("/signin")
    public String signinUser(UserSigninVO vo, RedirectAttributes redirectAttributes, HttpSession session) {
        R signin = memberFeignService.signin(vo);
        if (!signin.isSuccess()) {
            redirectAttributes.addFlashAttribute("errors", signin.message());
            return "redirect:http://auth.2cmall.com/signin.html";
        }
        MemberEntityDTO user = signin.get("user", new TypeReference<MemberEntityDTO>() {
        });
        session.setAttribute(AuthServerConstants.SESSION_KEY_LOGIN_USER, user);
        // 登录成功转到首页
        return "redirect:http://2cmall.com";
    }

    @GetMapping({"/", "/signin.html"})
    public String signinPage(HttpSession session) {
        val attribute = session.getAttribute(AuthServerConstants.SESSION_KEY_LOGIN_USER);
        if (attribute == null) {
            // 未登录
            return "signin";
        }
        return "redirect:http://2cmall.com";
    }
}
