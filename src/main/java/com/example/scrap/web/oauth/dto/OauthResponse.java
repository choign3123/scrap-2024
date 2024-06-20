package com.example.scrap.web.oauth.dto;

import lombok.Builder;
import lombok.Getter;

public class OauthResponse {

    @Builder
    @Getter
    public static class TokenDTO{
        private String accessToken;
        private String refreshToken;
    }
}
