package com.example.scrap.web.member;

import com.example.scrap.entity.Member;
import com.example.scrap.entity.enums.SnsType;
import com.example.scrap.jwt.dto.Token;
import com.example.scrap.web.member.dto.MemberRequest;
import com.example.scrap.web.member.dto.MemberDTO;

public interface IMemberQueryService {

    /**
     * 멤버 조회
     * @param memberDTO 멤버 식별자
     * @return
     */
    public Member findMember(MemberDTO memberDTO);

    /**
     * 멤버 조회
     */
    public Member findMember(String snsId, SnsType snsType);
}
