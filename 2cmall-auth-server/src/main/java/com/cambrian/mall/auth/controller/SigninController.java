package com.cambrian.mall.auth.controller;

import com.cambrian.common.utils.R;
import com.cambrian.mall.auth.feign.MemberFeignService;
import com.cambrian.mall.auth.vo.UserSigninVO;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
    public String signinUser(UserSigninVO vo, RedirectAttributes redirectAttributes) {
        R signin = memberFeignService.signin(vo);
        if (!signin.isSuccess()) {
            redirectAttributes.addFlashAttribute("errors", signin.message());
            return "redirect:http://auth.2cmall.com/signin.html";
        }
        // 登录成功转到首页
        return "redirect:http://2cmall.com";
    }
}
