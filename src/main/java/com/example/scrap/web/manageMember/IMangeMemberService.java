package com.example.scrap.web.manageMember;

import com.example.scrap.jwt.dto.Token;
import com.example.scrap.web.manageMember.dto.ManageMemberRequest;
import com.example.scrap.web.member.dto.MemberDTO;

public interface IMangeMemberService {

    /**
     * 토큰 유효성 검사
     */
    public Token validateToken(ManageMemberRequest.ValidateTokenDTO request);

    /**
     * 토큰 유효성 검사
     */
    public Token validateToken(String accessToken, String refreshToken);

    /**
     * 로그아웃
     */
    public void logout(MemberDTO memberDTO);
}
