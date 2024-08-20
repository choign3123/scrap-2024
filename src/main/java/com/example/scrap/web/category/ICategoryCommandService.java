package com.example.scrap.web.category;

import com.example.scrap.entity.Category;
import com.example.scrap.entity.Member;
import com.example.scrap.web.category.dto.CategoryRequest;
import com.example.scrap.web.member.dto.MemberDTO;

import java.util.List;

public interface ICategoryCommandService {

    /**
     * 카테고리 생성
     */
    public Category createCategory(MemberDTO memberDTO, CategoryRequest.CreateCategoryDTO request);

    /**
     * 기본 카테고리 생성
     */
    public Category createDefaultCategory(Member member);

    /**
     * 카테고리 삭제
     */
    public void deleteCategory(MemberDTO memberDTO, Long categoryId, Boolean allowDeleteScrap);

    /**
     * 모든 카테고리 삭제하기
     */
    public void deleteAllCategory(MemberDTO memberDTO);

    /**
     * 카테고리명 수정
     */
    public Category updateCategoryTitle(MemberDTO memberDTO, Long categoryId, CategoryRequest.UpdateCategoryTitleDTO request);

    /**
     * 카테고리 순서 변경
     */
    public List<Category> updateCategorySequence(MemberDTO memberDTO, CategoryRequest.UpdateCategorySequenceDTO request);
}
