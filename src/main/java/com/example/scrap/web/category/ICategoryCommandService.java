package com.example.scrap.web.category;

import com.example.scrap.entity.Category;
import com.example.scrap.entity.Member;
import com.example.scrap.web.category.dto.CategoryRequest;
import com.example.scrap.web.member.dto.MemberDTO;

import java.util.List;

public interface ICategoryCommandService {

    /**
     * 카테고리 생성
     * @param memberDTO
     * @param request
     * @return 생성된 카테고리
     */
    public Category createCategory(MemberDTO memberDTO, CategoryRequest.CreateCategoryDTO request);

    /**
     * 기본 카테고리 생성
     * @param member
     * @return
     */
    public Category createDefaultCategory(Member member);

    /**
     * 카테고리 삭제
     * @param memberDTO
     * @param categoryId 카테고리 식별자
     */
    public void deleteCategory(MemberDTO memberDTO, Long categoryId, Boolean allowDeleteScrap);

    /**
     * 모든 카테고리 삭제하기
     * @throws IllegalArgumentException 회원탈퇴한 사용자에 대해서만 해당 메소드 사용 가능
     */
    public void deleteAllCategory(MemberDTO memberDTO);

    /**
     * 카테고리명 수정
     * @param memberDTO
     * @param categoryId 카테고리 식별자
     * @param request
     * @return 수정된 카테고리
     */
    public Category updateCategoryTitle(MemberDTO memberDTO, Long categoryId, CategoryRequest.UpdateCategoryTitleDTO request);

    /**
     * 카테고리 순서 변경
     * @param memberDTO
     * @param request
     * @return
     */
    public List<Category> updateCategorySequence(MemberDTO memberDTO, CategoryRequest.UpdateCategorySequenceDTO request);
}
