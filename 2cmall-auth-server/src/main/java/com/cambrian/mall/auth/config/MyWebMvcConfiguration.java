package com.cambrian.mall.auth.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 配置页面映射关系
 *
 * @author kuma 2020-12-31
 */
@Configuration
public class MyWebMvcConfiguration implements WebMvcConfigurer {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // 适合纯页面跳转，不需要页面渲染的情况
//        registry.addViewController("/signin.html").setViewName("signin");
//        registry.addViewController("/").setViewName("signin");
        registry.addViewController("/register.html").setViewName("register");
    }
}
