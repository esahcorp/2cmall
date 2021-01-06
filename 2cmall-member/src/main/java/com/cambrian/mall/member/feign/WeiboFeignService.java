package com.cambrian.mall.member.feign;

import com.cambrian.mall.member.to.WeiboSocialUser;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author kuma 2021-01-04
 */
@FeignClient(value = "weibo-client", url = "https://api.weibo.com")
public interface WeiboFeignService {

    @GetMapping("/2/users/show.json")
    WeiboSocialUser showUser(@RequestParam("access_token") String accessToken,
                             @RequestParam("uid") String uid);
}
