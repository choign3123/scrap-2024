package com.example.scrap.web.category;

import com.example.scrap.entity.Category;
import com.example.scrap.entity.Member;
import com.example.scrap.web.member.dto.MemberDTO;
import com.example.scrap.base.exception.BaseException;

import java.util.List;

public interface ICategoryQueryService {


    /**
     * 카테고리 전체 조회
     */
    public List<Category> getCategoryWholeList(MemberDTO memberDTO);

    /**
     * 카테고리 찾기
     *
     * @throws BaseException 해당하는 카테고리가 존재하지 않는 경우
     * @throws BaseException 삭제된 카테고리인 경우
     */
    public Category findCategory(Long categoryId);

    /**
     * 카테고리 찾기
     *
     * @throws BaseException 해당하는 카테고리가 존재하지 않는 경우
     * @throws BaseException 삭제된 카테고리인 경우
     * @throws BaseException 찾은 카테고리의 멤버가 매개변수 멤버와 일치하지 않는 경우
     */
    public Category findCategory(Long categoryId, Member member);

    /**
     * 카테고리 목록 찾기
     *
     * @throws BaseException 리스트 중 존재하지 않는 카테고리가 있는 경우
     * @throws BaseException 리스트 중 삭제된 카테고리가 있는 경우
     */
    List<Category> findCategoryList(List<Long> categoryIdList);
}
