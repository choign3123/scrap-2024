package com.example.scrap.web.category;

import com.example.scrap.base.code.ErrorCode;
import com.example.scrap.base.exception.BaseException;
import com.example.scrap.entity.Category;
import com.example.scrap.entity.Member;
import com.example.scrap.entity.enums.CategoryStatus;
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
     */
    public List<Category> getCategoryWholeList(MemberDTO memberDTO){
        Member member = memberService.findMember(memberDTO);

        return categoryRepository.findAllByMemberAndStatusOrderBySequence(member, CategoryStatus.ACTIVE);
    }

    /**
     * 카테고리 찾기
     * @throws BaseException 삭제된 카테고리인 경우
     */
    public Category findCategory(Long categoryId){
        Category category = categoryRepository.findById(categoryId).get();

        if(category.getStatus().equals(CategoryStatus.DELETED)){
            throw new BaseException(ErrorCode.DELETED_CATEGORY);
        }

        return category;
    }

    /**
     * id로 카테고리 조회하기
     *
     * @throws BaseException 삭제된 카테고리인 경우
     * @throws BaseException id로 찾은 카테고리의 멤버가 매개변수 멤버와 일치하지 않는 경우
     */
    @Override
    public Category findCategory(Long categoryId, Member member) {
        Category category = findCategory(categoryId);

        if(category.isIllegalMember(member)){
            throw new BaseException(ErrorCode.CATEGORY_MEMBER_NOT_MATCH);
        }

        return category;
    }
}
