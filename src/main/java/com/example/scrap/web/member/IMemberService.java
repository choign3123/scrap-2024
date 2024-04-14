package com.example.scrap.web.member;

import com.example.scrap.entity.Member;

public interface IMemberService {

    /**
     * 멤버 조회
     * @param memberId 멤버 식별자
     * @return
     */
    public Member findMember(Long memberId);
}
