package com.example.scrap.converter;

import com.example.scrap.jwt.dto.Token;
import com.example.scrap.web.member.dto.MemberResponse;

public class ManageMemberConverter {

    public static MemberResponse.ValidateTokenDTO toValidateTokenDTO(Token token){

        return MemberResponse.ValidateTokenDTO.builder()
                .accessToken(token.getAccessToken())
                .refreshToken(token.getRefreshToken())
                .build();
    }
}
