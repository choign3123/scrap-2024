package com.example.scrap.web.manageMember;

import com.example.scrap.jwt.dto.Token;
import com.example.scrap.web.manageMember.dto.ManageMemberRequest;

public interface IMangeMemberService {

    /**
     * 토큰 유효성 검사
     */
    public Token validateToken(ManageMemberRequest.ValidateTokenDTO request);

    /**
     * 토큰 유효성 검사
     */
    public Token validateToken(String accessToken, String refreshToken);
}
