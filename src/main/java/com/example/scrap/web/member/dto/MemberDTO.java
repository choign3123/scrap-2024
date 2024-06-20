package com.example.scrap.web.member.dto;

import com.example.scrap.entity.Member;
import com.example.scrap.entity.enums.SnsType;
import lombok.Getter;

@Getter
public class MemberDTO {

    private final Long memberId;
    private final SnsType snsType;
    private final String snsId;

    public MemberDTO(Long memberId, SnsType snsType, String snsId) {
        this.memberId = memberId;
        this.snsType = snsType;
        this.snsId = snsId;
    }

    public MemberDTO(Member member){
        this.memberId = member.getId();
        this.snsId = member.getSnsId();
        this.snsType = member.getSnsType();
    }


    /**
     * 동일한 MemberDTO인지 확인
     * @return if snsType, snsId and memberId are all the same, return true. else return false.
     */
    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof MemberDTO)){ // obj가 MemberDTO가 아닐 경우
            throw new IllegalArgumentException(obj.getClass().getName() + "는 MemberDTO.class가 아닙니다.");
        }

        MemberDTO otherMemberDTO = (MemberDTO) obj;

        boolean isSnsIdSame = snsId.equals(otherMemberDTO.getSnsId());
        boolean isSnsTypeSame = (snsType == otherMemberDTO.getSnsType());
        boolean isMemberIdSame = memberId.equals(otherMemberDTO.getMemberId());

        return isSnsTypeSame && isSnsIdSame && isMemberIdSame;
    }

    /**
     * Member의 MemberDTO가 맞는지 확인
     * @param member 비교할 Member
     * @return if snsType, snsId and memberId are all the same, return true. else return false.
     */
    public boolean isMatchMember(Member member){
        boolean isSnsIdSame = snsId.equals(member.getSnsId());
        boolean isSnsTypeSame = (snsType == member.getSnsType());
        boolean isMemberIdSame = memberId.equals(member.getId());

        return isSnsTypeSame && isSnsIdSame && isMemberIdSame;
    }
}
