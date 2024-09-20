package com.example.scrap.converter;

import com.example.scrap.entity.Member;
import com.example.scrap.entity.MemberLog;
import com.example.scrap.entity.enums.SnsType;
import com.example.scrap.jwt.dto.Token;
import com.example.scrap.web.member.dto.MemberResponse.*;
import com.example.scrap.web.oauth.dto.CommonOauthMemberInfo;


public class MemberConverter {

    public static Member toEntity(CommonOauthMemberInfo memberInfo, SnsType snsType, MemberLog memberLog){

        return Member.builder()
                .name(memberInfo.getName())
                .snsType(snsType)
                .snsId(memberInfo.getSnsId())
                .memberLog(memberLog)
                .build();
    }

    public static ReissueTokenDTO toReissueTokenDTO(Token token){

        return ReissueTokenDTO.builder()
                .accessToken(token.getAccessToken())
                .refreshToken(token.getRefreshToken())
                .build();
    }

    public static TokenDTO toTokenDTO(Token token){
        return TokenDTO.builder()
                .accessToken(token.getAccessToken())
                .refreshToken(token.getRefreshToken())
                .build();
    }
}
