package com.example.scrap.jwt;


import com.example.scrap.entity.Member;
import com.example.scrap.jwt.dto.Token;
import com.example.scrap.jwt.dto.TokenType;
import com.example.scrap.web.member.dto.MemberDTO;

public interface ITokenProvider {

    /**
     * 토큰 발급하기
     */
    public Token createToken(Member member);

    /**
     * token 갱신하기
     */
    public Token reissueToken(String refreshToken, Member member);
    /* 토큰 발급 끝 **/

    /* 토큰 유효성 검사 **/

    /**
     * 토큰 유효성 검사.
     */
    public boolean isTokenValid(String token);

    /**
     * 토큰 타입 겁사
     */
    public boolean equalsTokenType(String token, TokenType tokenType);
    /* 토큰 유효성 검사 끝 **/

    /**
     * access 토큰을 MemberDTO로 변환
     */
    public MemberDTO parseAccessToMemberDTO(String accessToken);

    /**
     * refresh 토큰을 MemberDTO로 변환
     */
    public MemberDTO parseRefreshToMemberDTO(String refreshToken);

    /**
     * 토큰 prefix 지우기
     */
    public String removeTokenPrefix(String token);
}
