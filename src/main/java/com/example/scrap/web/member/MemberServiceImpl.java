package com.example.scrap.web.member;

import com.example.scrap.base.code.ErrorCode;
import com.example.scrap.base.exception.BaseException;
import com.example.scrap.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberServiceImpl implements IMemberService{

    private final MemberRepository memberRepository;

    /**
     * 멤버 조회
     * @param memberId 멤버 식별자
     * @return
     */
    public Member findMember(Long memberId){
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new BaseException(ErrorCode.MEMBER_NOT_FOUND));
    }

    public Member findMember(MemberDTO memberDTO){
        if(memberDTO.getMemberId() != null){
            return findMember(memberDTO.getMemberId());
        }
        else{
            throw new BaseException(ErrorCode._BAD_REQUEST);
        }
    }
}
