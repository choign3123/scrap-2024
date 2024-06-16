package com.example.scrap.web.category;

import com.example.scrap.entity.Category;
import com.example.scrap.entity.Member;
import com.example.scrap.web.member.dto.MemberDTO;

import java.util.List;

public interface ICategoryQueryService {


    /**
     * 카테고리 전체 조회
     * @param memberDTO
     * @return 전체 카테고리
     */
    public List<Category> getCategoryWholeList(MemberDTO memberDTO);

    /**
     * 기본 카테고리 찾기
     * @param member
     * @return
     */
    Category findDefaultCategory(Member member);

    public Category findCategory(Long categoryId);
}
