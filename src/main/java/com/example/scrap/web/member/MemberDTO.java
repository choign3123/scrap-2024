package com.example.scrap.web.member;

import lombok.Getter;

@Getter
public class MemberDTO {

    private Long memberId;

    public MemberDTO(Long memberId) {
        this.memberId = memberId;
    }
}
