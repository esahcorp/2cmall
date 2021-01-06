package com.cambrian.mall.member.to;

import lombok.Data;

/**
 * 用户注册信息
 *
 * @author kuma 2021-01-03
 */
@Data
public class MemberUserRegisterDTO {
    private String username;
    private String password;
    private String phone;
}
