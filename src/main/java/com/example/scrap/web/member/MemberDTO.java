package com.example.scrap.web.member;

import com.example.scrap.entity.enums.SnsType;
import lombok.Getter;

@Getter
public class MemberDTO {

    private Long memberId;

    private SnsType snsType;
    private String snsId;

    public MemberDTO(Long memberId) {
        this.memberId = memberId;
    }

    public MemberDTO(SnsType snsType, String snsId) {
        this.snsType = snsType;
        this.snsId = snsId;
    }
}
