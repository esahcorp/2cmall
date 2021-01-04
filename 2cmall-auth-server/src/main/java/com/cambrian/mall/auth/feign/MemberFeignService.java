package com.cambrian.mall.auth.feign;

import com.cambrian.common.utils.R;
import com.cambrian.mall.auth.vo.UserRegisterVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author kuma 2021-01-04
 */
@FeignClient(value = "2cmall-member", path = "/member/member")
public interface MemberFeignService {

    @PostMapping("/register")
    R register(@RequestBody UserRegisterVO registerVO);
}
