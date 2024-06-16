package com.example.scrap.web.member;

import com.example.scrap.entity.Member;
import com.example.scrap.web.oauth.dto.NaverResponse;

public interface IMemberService {

    /**
     * 멤버 조회
     * @param memberDTO 멤버 식별자
     * @return
     */
    public Member findMember(MemberDTO memberDTO);

    /**
     * 네이버 회원가입
     * @param profileInfo
     * @return
     */
    public Member signup(NaverResponse.ProfileInfo.Response profileInfo);
}
