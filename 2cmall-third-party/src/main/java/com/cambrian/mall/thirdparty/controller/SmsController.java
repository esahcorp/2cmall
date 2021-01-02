package com.cambrian.mall.thirdparty.controller;

import com.cambrian.common.utils.R;
import com.cambrian.mall.thirdparty.service.SmsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author kuma 2021-01-02
 */
@RequestMapping("/sms")
@RestController
public class SmsController {

    private final SmsService smsService;

    public SmsController(SmsService smsService) {
        this.smsService = smsService;
    }

    @GetMapping("/verification")
    public R sendSmsVerificationCode(@RequestParam("verificationCode") String verificationCode,
                                     @RequestParam("phoneNumber") String phoneNumber) {
        smsService.sendVerificationCode(verificationCode, phoneNumber);
        // just mock
        return R.ok();
    }
}
