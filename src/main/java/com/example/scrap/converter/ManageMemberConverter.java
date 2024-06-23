package com.example.scrap.converter;

import com.example.scrap.jwt.dto.Token;
import com.example.scrap.web.manageMember.dto.ManageMemberResponse;

public class ManageMemberConverter {

    public static ManageMemberResponse.ValidateTokenDTO toValidateTokenDTO(Token token){

        return ManageMemberResponse.ValidateTokenDTO.builder()
                .accessToken(token.getAccessToken())
                .refreshToken(token.getRefreshToken())
                .build();
    }
}
