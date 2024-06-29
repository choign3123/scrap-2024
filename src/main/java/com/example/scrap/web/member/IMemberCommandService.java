package com.example.scrap.web.member;

import com.example.scrap.entity.Member;
import com.example.scrap.web.oauth.dto.NaverResponse;

public interface IMemberCommandService {

    /**
     * 네이버 회원가입
     * @param profileInfo
     * @return
     */
    public Member signup(NaverResponse.ProfileInfo.Response profileInfo);

    /**
     * 회원 탈퇴
     */
    public void signOut(Member member);
}
