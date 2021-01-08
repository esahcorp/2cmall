package com.cambrian.mall.auth.controller;

import com.alibaba.fastjson.TypeReference;
import com.cambrian.common.constant.AuthServerConstants;
import com.cambrian.common.to.MemberEntityDTO;
import com.cambrian.mall.auth.config.WeiboOAuth2Properties;
import com.cambrian.mall.auth.feign.MemberFeignService;
import com.cambrian.mall.auth.to.WeiboAccessTokenDTO;
import lombok.val;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpSession;

/**
 * @author kuma 2021-01-04
 */
@Controller
@RequestMapping("/oauth2.0")
public class Auth2Controller {

    private final WeiboOAuth2Properties weiboOAuth2Properties;
    private final RestTemplate restTemplate;
    private final MemberFeignService memberClient;

    public Auth2Controller(WeiboOAuth2Properties weiboOauth2Properties,
                           RestTemplate restTemplate,
                           MemberFeignService memberClient) {
        this.weiboOAuth2Properties = weiboOauth2Properties;
        this.restTemplate = restTemplate;
        this.memberClient = memberClient;
    }

    @GetMapping("/weibo/success")
    public String webbo(@RequestParam("code") String code, HttpSession session) {
        // 1. 获取 access token
        val param = weiboOAuth2Properties.buildTokenParam(code);
        val header = new HttpHeaders();
        header.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        val httpEntity = new HttpEntity<>(param, header);
        ResponseEntity<WeiboAccessTokenDTO> accessToken =
                restTemplate.postForEntity(weiboOAuth2Properties.tokenUrl(), httpEntity, WeiboAccessTokenDTO.class);
        if (accessToken.getStatusCode() != HttpStatus.OK) {
            return "redirect:http://auth.2cmall.com/signin.html";
        }
        val token = accessToken.getBody();
        // 2. 调用远程服务查询用户信息
        try {
            val result = memberClient.oauth2Signin(token);
            if (!result.isSuccess()) {
                return "redirect:http://auth.2cmall.com/signin.html";
            }
            MemberEntityDTO user = result.get("social_user", new TypeReference<MemberEntityDTO>() {
            });
            session.setAttribute(AuthServerConstants.SESSION_KEY_LOGIN_USER, user);
            // 3. 跳回首页
            return "redirect:http://2cmall.com";
        } catch (Exception e) {
            return "redirect:http://auth.2cmall.com/signin.html";
        }
    }


}
