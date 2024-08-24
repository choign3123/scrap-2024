package com.example.scrap.web.mypage;

import com.example.scrap.entity.Member;
import com.example.scrap.entity.enums.CategoryStatus;
import com.example.scrap.web.category.CategoryRepository;
import com.example.scrap.web.member.IMemberQueryService;
import com.example.scrap.web.member.dto.MemberDTO;
import com.example.scrap.web.mypage.dto.MypageResponse.*;
import com.example.scrap.web.scrap.ScrapRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MypageQueryServiceImpl implements IMypageQueryService {

    private final IMemberQueryService memberQueryService;
    private final CategoryRepository categoryRepository;
    private final ScrapRepository scrapRepository;

    /**
     * 마이페이지 조회
     */
    public MypageDTO mypage(MemberDTO memberDTO){
        Member member = memberQueryService.findMember(memberDTO);

        // 총 카테고리 개수
        long totalCategory = categoryRepository.countByMemberAndStatus(member, CategoryStatus.ACTIVE);

        // 총 스크랩 개수
        long totalScrap = scrapRepository.countAllByMember(member);

        return MypageDTO.builder()
                .name(member.getName())
                .totalCategory(totalCategory)
                .totalScrap(totalScrap)
                .build();
    }
}
