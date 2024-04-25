package com.example.scrap.web.category;

import com.example.scrap.entity.Category;
import com.example.scrap.entity.Member;
import com.example.scrap.web.category.dto.CategoryRequest;
import com.example.scrap.web.category.dto.CategoryResponse;

import java.util.List;

public interface ICategoryService {

    /**
     * 카테고리 생성
     * @param member
     * @param request
     * @return 생성된 카테고리
     */
    public Category createCategory(Member member, CategoryRequest.CreateCategoryDTO request);

    /**
     * 카테고리 전체 조회
     * @param member
     * @return 전체 카테고리
     */
    public List<Category> getCategoryWholeList(Member member);
}
