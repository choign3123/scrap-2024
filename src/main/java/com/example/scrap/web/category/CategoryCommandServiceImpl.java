package com.example.scrap.web.category;

import com.example.scrap.base.code.ErrorCode;
import com.example.scrap.base.data.PolicyData;
import com.example.scrap.base.exception.BaseException;
import com.example.scrap.converter.CategoryConverter;
import com.example.scrap.entity.Category;
import com.example.scrap.entity.Member;
import com.example.scrap.entity.Scrap;
import com.example.scrap.base.data.DefaultData;
import com.example.scrap.web.category.dto.CategoryRequest;
import com.example.scrap.web.member.IMemberQueryService;
import com.example.scrap.web.member.dto.MemberDTO;
import com.example.scrap.web.scrap.IScrapCommandService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class CategoryCommandServiceImpl implements ICategoryCommandService {

    private final CategoryRepository categoryRepository;
    private final ICategoryQueryService categoryQueryService;
    private final IMemberQueryService memberService; // TODO: 메소드명 바꾸기
    private final IScrapCommandService scrapCommandService;

    /**
     * 카테고리 생성
     * @throws BaseException 카테고리 생성 제한 개수 초과
     * @throws NoSuchElementException 카테고리의 max sequence를 찾을 수 없을 시
     */
    public Category createCategory(MemberDTO memberDTO, CategoryRequest.CreateCategoryDTO request){
        Member member = memberService.findMember(memberDTO);

        // 카테고리 생성 개수 제한 확인
        boolean isExceedCategoryLimit = member.getCategoryList().size() >= PolicyData.CATEGORY_CREATE_LIMIT;
        if(isExceedCategoryLimit){
            throw new BaseException(ErrorCode.EXCEED_CATEGORY_CREATE_LIMIT);
        }

        int newCategorySequence = categoryRepository.findMaxSequenceByMember(member)
                .orElseThrow(() -> new NoSuchElementException("카테고리의 max sequence를 찾을 수 없음")) + 1;

        Category newCategory = CategoryConverter.toEntity(member, request, newCategorySequence);

        categoryRepository.save(newCategory);

        return newCategory;
    }

    /**
     * 기본 카테고리 생성
     * @param member
     * @return
     */
    public Category createDefaultCategory(Member member){

        Category defaultCategory = CategoryConverter.toEntity(member, PolicyData.DEFAULT_CATEGORY_TITLE, true, 1);

        categoryRepository.save(defaultCategory);

        return defaultCategory;
    }

    /**
     * 카테고리 삭제
     * @param memberDTO
     * @param categoryId 카테고리 식별자
     */
    public void deleteCategory(MemberDTO memberDTO, Long categoryId){
        Member member = memberService.findMember(memberDTO);
        Category category = categoryQueryService.findCategory(categoryId);

        category.checkIllegalMember(member);

        // 기본 카테고리는 삭제할 수 없음
        if(category.getIsDefault()){
            throw new BaseException(ErrorCode.NOT_ALLOW_ACCESS_DEFAULT_CATEGORY);
        }

        // 카테고리에 속한 모든 스크랩 휴지통으로 이동
        List<Scrap> scrapList = new ArrayList<>(category.getScrapList());
        for(Scrap scrap : scrapList){
            scrapCommandService.throwScrapIntoTrash(scrap);
        }

        categoryRepository.delete(category);
    }

    /**
     * 모든 카테고리 삭제하기
     */
    public void deleteAllCategory(MemberDTO memberDTO){
        Member member = memberService.findMember(memberDTO);

        categoryRepository.deleteAllByMember(member);
    }

    /** 
     * 카테고리명 수정
     * @param memberDTO
     * @param categoryId 카테고리 식별자
     * @param request
     * @return 수정된 카테고리
     */
    public Category updateCategoryTitle(MemberDTO memberDTO, Long categoryId, CategoryRequest.UpdateCategoryTitleDTO request){
        Member member = memberService.findMember(memberDTO);
        Category category = categoryQueryService.findCategory(categoryId);

        // 카테고리를 만든 사용자가 맞는지 확인
        category.checkIllegalMember(member);

        // 기본 카테고리명은 수정 불가
        if(category.getIsDefault()){
            throw new BaseException(ErrorCode.NOT_ALLOW_ACCESS_DEFAULT_CATEGORY);
        }

        category.updateTitle(request.getNewCategoryTitle());

        return category;
    }

    /**
     * 카테고리 순서 변경
     * @return 새로운 순서로 정렬된 카테고리 목록
     */
    public List<Category> updateCategorySequence(MemberDTO memberDTO, CategoryRequest.UpdateCategorySequenceDTO request){
        Member member = memberService.findMember(memberDTO);

        if(request.getCategoryList().size() != member.getCategoryList().size()){
            throw new BaseException(ErrorCode.REQUEST_CATEGORY_COUNT_NOT_ALL);
        }

        // 정렬된 카테고리 id에 sequence 순서대로 부여
        Map<Long, Integer> changeSequenceMap = new HashMap<>();
        int newSequence = 1;
        for(Long categoryId : request.getCategoryList()){
            changeSequenceMap.put(categoryId, newSequence);
            newSequence++;
        }

        // 순서 변경
        for(Category category : member.getCategoryList()){
            category.changeSequence(changeSequenceMap.get(category.getId()));
        }

        Collections.sort(member.getCategoryList());
        return member.getCategoryList();
    }

}
