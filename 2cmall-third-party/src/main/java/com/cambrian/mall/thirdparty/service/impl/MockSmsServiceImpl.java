package com.cambrian.mall.thirdparty.service.impl;

import com.cambrian.common.utils.R;
import com.cambrian.mall.thirdparty.feign.SmsFeignService;
import com.cambrian.mall.thirdparty.service.SmsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author kuma 2021-01-02
 */
@Service
@Slf4j
public class MockSmsServiceImpl implements SmsService {

    private final SmsFeignService smsFeignService;

    public MockSmsServiceImpl(SmsFeignService smsFeignService) {
        this.smsFeignService = smsFeignService;
    }

    @Override
    public R sendVerificationCode(String verificationCode, String phoneNumber) {
        log.info("Mocking send a verification code {} to {}", verificationCode, phoneNumber);
        String result = smsFeignService.send();
        log.info("Result: {}", result);
        return R.ok();
    }
}
