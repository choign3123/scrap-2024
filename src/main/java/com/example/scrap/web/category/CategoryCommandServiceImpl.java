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
    private final IMemberQueryService memberService;

    /**
     * 카테고리 생성
     * @param memberDTO
     * @param request
     * @return 생성된 카테고리
     */
    public Category createCategory(MemberDTO memberDTO, CategoryRequest.CreateCategoryDTO request){
        Member member = memberService.findMember(memberDTO);

        // 카테고리 생성 개수 제한 확인
        boolean isExceedCategoryLimit = member.getCategoryList().size() >= PolicyData.CATEGORY_CREATE_LIMIT;
        if(isExceedCategoryLimit){
            throw new BaseException(ErrorCode.EXCEED_CATEGORY_CREATE_LIMIT);
        }

        Category newCategory = CategoryConverter.toEntity(member, request);

        categoryRepository.save(newCategory);

        return newCategory;
    }

    /**
     * 기본 카테고리 생성
     * @param member
     * @return
     */
    public Category createDefaultCategory(Member member){

        Category defaultCategory = CategoryConverter.toEntity(member, PolicyData.DEFAULT_CATEGORY_TITLE, true);

        return categoryRepository.save(defaultCategory);
    }

    /**
     * 카테고리 삭제
     * @param memberDTO
     * @param categoryId 카테고리 식별자
     */
    public void deleteCategory(MemberDTO memberDTO, Long categoryId, Boolean allowDeleteScrap){
        Member member = memberService.findMember(memberDTO);
        Category category = categoryRepository.findById(categoryId).get();

        category.checkIllegalMember(member);

        // 기본 카테고리는 삭제할 수 없음
        if(category.getIsDefault()){
            throw new BaseException(ErrorCode.NOT_ALLOW_ACCESS_DEFAULT_CATEGORY);
        }

        // [TODO] 리스트를 복제해서 for 문을 돌리는것 외에 다른 방법은 없는지 고민해봐야 될 것 같음. 이렇게 복제된 리스트를 사용하지 않으면, 요소 삭제로 인해 for 문을 다 돌지 못하고 끝나버림.
        // 모든 스크랩은 기본 카테고리로 이동
        Category defaultCategory = categoryQueryService.findDefaultCategory(member);
        List<Scrap> scrapListCopy = new ArrayList<>(category.getScrapList());
        for(Scrap scrap : scrapListCopy){
            scrap.moveCategory(defaultCategory);
        }

        // 스크랩을 삭제하기로 결정한 경우
        if(allowDeleteScrap){
            for(Scrap scrap : category.getScrapList()){
                scrap.toTrash();
            }
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
        Category category = categoryRepository.findById(categoryId).get();

        if(category.checkIllegalMember(member)){
            throw new BaseException(ErrorCode.CATEGORY_MEMBER_NOT_MATCH);
        }

        // 기본 카테고리명은 수정 불가
        if(category.getIsDefault()){
            throw new BaseException(ErrorCode.NOT_ALLOW_ACCESS_DEFAULT_CATEGORY);
        }

        category.updateTitle(request.getNewCategoryTitle());

        return category;
    }

    /**
     * 카테고리 순서 변경
     * @param memberDTO
     * @param request
     * @return
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
