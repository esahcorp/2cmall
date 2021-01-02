package com.cambrian.mall.thirdparty.service;

import com.cambrian.common.utils.R;

/**
 * @author kuma 2021-01-02
 */
public interface SmsService {

    R sendVerificationCode(String verificationCode, String phoneNumber);
}
