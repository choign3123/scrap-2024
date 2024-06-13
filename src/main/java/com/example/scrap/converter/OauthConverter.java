package com.example.scrap.converter;

import com.example.scrap.web.baseDTO.Token;
import com.example.scrap.web.oauth.dto.OauthResponse;

public class OauthConverter {

    public static OauthResponse.TokenDTO toTokenDTO(Token token){
        return OauthResponse.TokenDTO.builder()
                .accessToken(token.getAccessToken())
                .refreshToken(token.getRefreshToken())
                .build();
    }
}
