package com.cambrian.mall.member.to;

import com.cambrian.mall.member.entity.MemberEntity;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import lombok.var;

@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class WeiboSocialUser {
    private String avatarLarge;
    private String gender;
    private String name;

    public MemberEntity transToMember() {
        var member = new MemberEntity();
        member.setHeader(avatarLarge);
        member.setGender("M".equalsIgnoreCase(gender) ? 1 : 0);
        member.setUsername(name);
        return member;
    }
}
