package com.example.scrap.web.member;

import com.example.scrap.entity.enums.SnsType;
import com.example.scrap.jwt.dto.Token;
import com.example.scrap.web.member.dto.MemberDTO;

public interface IMemberCommandService {


    /**
     * 로그인/회원가입 (통합)
     */
    public Token integrationLoginSignup(String authorization, SnsType snsType);

    /**
     * 토큰 재발급
     */
    public Token reissueToken(String refreshToken);

    /**
     * 로그아웃
     */
    public void logout(MemberDTO memberDTO, String token);

    /**
     * 회원 탈퇴
     */
    public void signOut(MemberDTO memberDTO);
}
