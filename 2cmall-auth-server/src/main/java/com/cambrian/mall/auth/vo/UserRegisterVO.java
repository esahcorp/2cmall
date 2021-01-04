package com.cambrian.mall.auth.vo;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

/**
 * 用户注册信息
 *
 * @author kuma 2021-01-03
 */
@Data
public class UserRegisterVO {
    @NotBlank(message = "请输入 6 到 18 位的用户名")
    @Length(min = 6, max = 18, message = "请输入 6 到 18 位的用户名")
    private String username;
    @NotEmpty(message = "请输入密码")
    @Pattern(regexp = "^[\\w_-]{6,18}$", message = "请输入 6 到 18 位密码")
    private String password;
    @NotEmpty(message = "请输入手机号")
    @Pattern(regexp = "^1[3-9][\\d]{9}$", message = "请输入格式正确的手机号")
    private String phone;
    @NotBlank(message = "请输入验证码")
    @Length(min = 6, max = 6, message = "请输入验证码")
    private String code;
}
