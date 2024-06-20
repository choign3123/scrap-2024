package com.example.scrap.web.mypage;

import com.example.scrap.converter.MypageConverter;
import com.example.scrap.entity.Member;
import com.example.scrap.web.member.IMemberQueryService;
import com.example.scrap.web.member.MemberRepository;
import com.example.scrap.web.member.dto.MemberDTO;
import com.example.scrap.web.mypage.dto.MypageResponse.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MypageQueryServiceImpl implements IMypageQueryService {

    private final IMemberQueryService memberService;
    private final MemberRepository memberRepository;

    /**
     * 마이페이지 조회
     * @param memberDTO
     * @return
     */
    public MypageDTO mypage(MemberDTO memberDTO){
        Member member = memberService.findMember(memberDTO);

        MypageDTO.Statistics statistics = memberRepository.getMypageStatistics(member);
        return MypageConverter.toMypage(member, statistics);
    }
}
