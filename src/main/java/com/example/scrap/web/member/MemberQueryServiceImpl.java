package com.example.scrap.web.member;

import com.example.scrap.base.code.ErrorCode;
import com.example.scrap.base.exception.BaseException;
import com.example.scrap.entity.Member;
import com.example.scrap.entity.enums.SnsType;
import com.example.scrap.web.member.dto.MemberDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberQueryServiceImpl implements IMemberQueryService {

    private final MemberRepository memberRepository;

    /**
     * 멤버 조회
     * @throws BaseException 해당하는 멤버를 찾지 못했을 경우
     */
    public Member findMember(MemberDTO memberDTO){

        if(memberDTO.getMemberId().isPresent()){
            return memberRepository.findById(memberDTO.getMemberId().get())
                    .orElseThrow(() -> new BaseException(ErrorCode.MEMBER_NOT_FOUND));
        }
        else{
            return findMember(memberDTO.getSnsId(), memberDTO.getSnsType());
        }
    }

    /**
     * 멤버 조회
     * @throws BaseException 해당하는 멤버를 찾지 못했을 경우
     */
    public Member findMember(String snsId, SnsType snsType){
        return memberRepository.findBySnsTypeAndSnsId(snsType, snsId)
                .orElseThrow(() -> new BaseException(ErrorCode.MEMBER_NOT_FOUND));
    }

    /**
     * MemberLog fetch join한 Member 조회
     */
    public Member findMemberWithLog(MemberDTO memberDTO) {

        if(memberDTO.getMemberId().isPresent()){
            return memberRepository.findByIdWithMemberLog(memberDTO.getMemberId().get())
                    .orElseThrow(() -> new BaseException(ErrorCode.MEMBER_NOT_FOUND));
        }
        else{
            return memberRepository.findBySnsTypeAndSnsIdWithMemberLog(memberDTO.getSnsType(), memberDTO.getSnsId())
                    .orElseThrow(() -> new BaseException(ErrorCode.MEMBER_NOT_FOUND));
        }
    }
}
