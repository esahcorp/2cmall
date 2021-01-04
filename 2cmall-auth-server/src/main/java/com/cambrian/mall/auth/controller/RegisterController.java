package com.cambrian.mall.auth.controller;

import com.cambrian.common.constant.AuthServerConstants;
import com.cambrian.common.utils.R;
import com.cambrian.mall.auth.feign.MemberFeignService;
import com.cambrian.mall.auth.feign.ThirdPartFeignService;
import com.cambrian.mall.auth.utils.SequenceUtils;
import com.cambrian.mall.auth.vo.UserRegisterVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 登录接口
 *
 * @author kuma 2021-01-02
 */
@Controller
public class RegisterController {

    private static final int SENDING_INTERVAL_TIME = 60_000;

    private final ThirdPartFeignService thirdPartFeignService;
    private final StringRedisTemplate stringRedisTemplate;
    private final MemberFeignService memberFeignService;

    public RegisterController(ThirdPartFeignService thirdPartFeignService,
                              StringRedisTemplate stringRedisTemplate,
                              MemberFeignService memberFeignService) {
        this.thirdPartFeignService = thirdPartFeignService;
        this.stringRedisTemplate = stringRedisTemplate;
        this.memberFeignService = memberFeignService;
    }

    @GetMapping("/sms/verification")
    @ResponseBody
    public R sendVerification(String phoneNumber) {
        // 判断手机号是否在 60s 内发送过一次验证码
        String cacheKey = AuthServerConstants.CacheKey.VERIFICATION_PREFIX + phoneNumber;
        String val = stringRedisTemplate.opsForValue().get(cacheKey);
        if (StringUtils.isNotBlank(val)) {
            long previous = Long.parseLong(val.substring(val.lastIndexOf("_") + 1));
            if (System.currentTimeMillis() - previous < SENDING_INTERVAL_TIME) {
                return R.error("请等候 60 秒之后再重新发送验证码");
            }
        }
        String verification = SequenceUtils.getSixDigits();
        // 保存验证码时存储当前时间，以判断再次调用接口时是否可以重新发送验证码
        String cacheVal = verification + "_" + System.currentTimeMillis();
        // 验证码需要在注册接口二次校验，采用存入 redis 的方式，key: 包含 phone, value: verification code
        stringRedisTemplate.opsForValue().set(cacheKey, cacheVal, 10, TimeUnit.MINUTES);
        thirdPartFeignService.sendSmsVerificationCode(verification, phoneNumber);
        return R.ok();
    }

    @PostMapping("/register")
    public String register(@Valid UserRegisterVO registerVO, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        // 1. 提交数据校验
        String redirectToReg = "redirect:http://auth.2cmall.com/register.html";
        if (bindingResult.hasErrors()) {
            HashMap<String, String> errMap = bindingResult.getFieldErrors().stream()
                    .collect(HashMap::new, (map, fe) -> map.put(fe.getField(), fe.getDefaultMessage()), HashMap::putAll);
            redirectAttributes.addFlashAttribute("errors", errMap);
            return redirectToReg;
        }
        // 2. 检查验证码
        String code = registerVO.getCode();
        String key = AuthServerConstants.CacheKey.VERIFICATION_PREFIX + registerVO.getPhone();
        String val = stringRedisTemplate.opsForValue().get(key);
        Map<String, String> errors = new HashMap<>(1);
        if (StringUtils.isBlank(val)) {
            // 2.1 redis 中的验证码为空 -> 已经过期
            errors.put("code", "验证码已经过期，请重新获取");
            redirectAttributes.addFlashAttribute("errors", errors);
            return redirectToReg;
        }
        String expect = val.substring(0, val.lastIndexOf("_"));
        if (!code.equals(expect)) {
            // 2.2 验证码不一致
            errors.put("code", "请输入正确的验证码");
            redirectAttributes.addFlashAttribute("errors", errors);
            return redirectToReg;
        }
        // 3. 验证码验证成功，需要删除缓存
        stringRedisTemplate.delete(key);
        // 4. 远程注册接口
        R result = memberFeignService.register(registerVO);
        if (!result.isSuccess()) {
            errors.put("msg", result.message());
            redirectAttributes.addFlashAttribute("errors", errors);
            return redirectToReg;
        }
        // 5. 注册成功重定向到登录页
        return "redirect:http://auth.2cmall.com/signin.html";
    }

}
