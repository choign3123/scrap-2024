package com.example.scrap.web.member;

import com.example.scrap.converter.MemberConverter;
import com.example.scrap.entity.Member;
import com.example.scrap.entity.MemberLog;
import com.example.scrap.entity.enums.SnsType;
import com.example.scrap.web.category.CategoryRepository;
import com.example.scrap.web.category.ICategoryCommandService;
import com.example.scrap.web.oauth.dto.NaverResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberCommandServiceImpl implements IMemberCommandService {

    private final MemberRepository memberRepository;
    private final ICategoryCommandService categoryCommandService;

    /**
     * 네이버 회원가입
     * @param profileInfo
     * @return
     */
    public Member signup(NaverResponse.ProfileInfo.Response profileInfo){
        MemberLog memberLog = new MemberLog();
        Member member = MemberConverter.toEntity(profileInfo, SnsType.NAVER, memberLog);

        // 기본 카테고리 생성
        categoryCommandService.createDefaultCategory(member);

        return memberRepository.save(member);
    }

    /**
     * 회원 탈퇴
     */
    public void signOut(Member member){
        memberRepository.delete(member);
    }
}
