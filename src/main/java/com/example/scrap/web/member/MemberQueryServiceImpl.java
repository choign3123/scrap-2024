package com.example.scrap.web.member;

import com.example.scrap.base.code.ErrorCode;
import com.example.scrap.base.exception.BaseException;
import com.example.scrap.converter.MemberConverter;
import com.example.scrap.entity.Member;
import com.example.scrap.entity.MemberLog;
import com.example.scrap.entity.enums.SnsType;
import com.example.scrap.web.category.ICategoryCommandService;
import com.example.scrap.web.member.dto.MemberDTO;
import com.example.scrap.web.oauth.dto.NaverResponse;
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
     */
    public Member findMember(MemberDTO memberDTO){
        return memberRepository.findById(memberDTO.getMemberId())
                .orElseThrow(() -> new BaseException(ErrorCode.MEMBER_NOT_FOUND));
    }
}
