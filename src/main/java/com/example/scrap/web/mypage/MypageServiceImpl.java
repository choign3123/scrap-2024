package com.example.scrap.web.mypage;

import com.example.scrap.converter.MypageConverter;
import com.example.scrap.entity.Member;
import com.example.scrap.entity.Scrap;
import com.example.scrap.specification.ScrapSpecification;
import com.example.scrap.web.member.IMemberService;
import com.example.scrap.web.member.MemberDTO;
import com.example.scrap.web.mypage.dto.MypageResponse;
import com.example.scrap.web.scrap.ScrapRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MypageServiceImpl implements IMypageService{

    private final IMemberService memberService;
    private final ScrapRepository scrapRepository;

    /**
     * 마이페이지 조회
     * @param memberDTO
     * @return
     */
    public MypageResponse.Mypage mypage(MemberDTO memberDTO){
        Member member = memberService.findMember(memberDTO);

        Specification<Scrap> spec = Specification.where(ScrapSpecification.isAvailable())
                .and(ScrapSpecification.equalMember(member));

        int totalScrap = Long.valueOf(scrapRepository.count(spec)).intValue();

        return MypageConverter.toMypage(totalScrap);
    }
}
