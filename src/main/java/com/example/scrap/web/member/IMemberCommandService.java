package com.example.scrap.web.member;

import com.example.scrap.entity.Member;
import com.example.scrap.jwt.dto.Token;
import com.example.scrap.web.member.dto.MemberDTO;
import com.example.scrap.web.oauth.dto.NaverResponse;
import org.springframework.transaction.annotation.Transactional;

public interface IMemberCommandService {

    /**
     * 네이버 로그인
     */
    public Token login(String authorization);

    /**
     * 네이버 회원가입
     */
    public Member signup(NaverResponse.ProfileInfo.Response profileInfo);

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
