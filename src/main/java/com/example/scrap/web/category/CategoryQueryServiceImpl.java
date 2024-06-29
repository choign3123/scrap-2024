package com.example.scrap.web.category;

import com.example.scrap.base.code.ErrorCode;
import com.example.scrap.base.exception.BaseException;
import com.example.scrap.entity.Category;
import com.example.scrap.entity.Member;
import com.example.scrap.web.member.IMemberQueryService;
import com.example.scrap.web.member.dto.MemberDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class CategoryQueryServiceImpl implements ICategoryQueryService {

    private final CategoryRepository categoryRepository;
    private final IMemberQueryService memberService;


    /**
     * 카테고리 전체 조회
     * @param memberDTO
     * @return 전체 카테고리
     */
    public List<Category> getCategoryWholeList(MemberDTO memberDTO){
        Member member = memberService.findMember(memberDTO);

        return categoryRepository.findAllByMemberOrderBySequence(member);
    }

    /**
     * 기본 카테고리 찾기
     * @param member
     * @return
     */
    public Category findDefaultCategory(Member member){
        return categoryRepository.findByMemberAndIsDefaultTrue(member)
                .orElseThrow(() -> new BaseException(ErrorCode.CATEGORY_NOT_FOUND));
    }

    /**
     * 카테고리 찾기
     * @param categoryId
     * @return
     */
    public Category findCategory(Long categoryId){
        return categoryRepository.findById(categoryId).get();
    }
}
