package com.example.scrap.web.member;

import com.example.scrap.base.code.ErrorCode;
import com.example.scrap.base.exception.BaseException;
import com.example.scrap.converter.CategoryConverter;
import com.example.scrap.converter.MemberConverter;
import com.example.scrap.entity.Category;
import com.example.scrap.entity.Member;
import com.example.scrap.entity.enums.SnsType;
import com.example.scrap.web.baseDTO.Data;
import com.example.scrap.web.category.CategoryRepository;
import com.example.scrap.web.category.ICategoryCommandService;
import com.example.scrap.web.oauth.dto.NaverResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberCommandServiceImpl implements IMemberCommandService {

    private final MemberRepository memberRepository;
    private final ICategoryCommandService categoryCommandService;
    private final CategoryRepository categoryRepository;

    /**
     * 네이버 회원가입
     * @param profileInfo
     * @return
     */
    public Member signup(NaverResponse.ProfileInfo.Response profileInfo){
        Member member = MemberConverter.toEntity(profileInfo, SnsType.NAVER);

        // 기본 카테고리 생성
        categoryCommandService.createDefaultCategory(member);

        return memberRepository.save(member);
    }
}
