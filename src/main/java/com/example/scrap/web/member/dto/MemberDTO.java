package com.example.scrap.web.member.dto;

import com.example.scrap.entity.Member;
import com.example.scrap.entity.enums.SnsType;
import lombok.Builder;
import lombok.Getter;

import java.util.Optional;

@Getter
public class MemberDTO {

    private final Long memberId;
    private final SnsType snsType;
    private final String snsId;

    @Builder
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
        if(!(obj instanceof MemberDTO otherMemberDTO)){ // obj가 MemberDTO가 아닐 경우
            throw new IllegalArgumentException(obj.getClass().getName() + "는 MemberDTO.class가 아닙니다.");
        }

        return isMatchValue(otherMemberDTO.memberId, otherMemberDTO.snsId, otherMemberDTO.snsType);
    }

    /**
     * Member의 MemberDTO가 맞는지 확인
     * @param member 비교할 Member
     * @return if snsType, snsId and memberId are all the same, return true. else return false.
     */
    public boolean isMatchMember(Member member){
        return isMatchValue(member.getId(), member.getSnsId(), member.getSnsType());
    }

    private boolean isMatchValue(Long memberId, String snsId, SnsType snsType){
        boolean isSnsIdSame = this.snsId.equals(snsId);
        boolean isSnsTypeSame = (this.snsType == snsType);
        boolean isMemberIdSame;
        if(this.memberId == null){
            isMemberIdSame = true;
        }
        else{
            isMemberIdSame = this.memberId.equals(memberId);
        }

        return isSnsTypeSame && isSnsIdSame && isMemberIdSame;
    }

    public Optional<Long> getMemberId(){
        return Optional.ofNullable(memberId);
    }
}
