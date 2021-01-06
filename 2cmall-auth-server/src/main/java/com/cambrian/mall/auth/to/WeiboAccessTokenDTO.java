package com.cambrian.mall.auth.to;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

/**
 * @author kuma
 */
@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class WeiboAccessTokenDTO {
    private String accessToken;
    private String uid;
    private Long expiresIn;
}
