package com.cambrian.mall.auth.app;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author kuma 2020-12-30
 */
@Controller
public class SigninController {

    @GetMapping({"/", "/signin.html"})
    public String signin() {
        return "signin";
    }
}
