package com.example.scrap.web.category;

import com.example.scrap.base.code.ErrorCode;
import com.example.scrap.base.data.PolicyData;
import com.example.scrap.base.exception.BaseException;
import com.example.scrap.converter.CategoryConverter;
import com.example.scrap.entity.Category;
import com.example.scrap.entity.Member;
import com.example.scrap.entity.Scrap;
import com.example.scrap.entity.enums.CategoryStatus;
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
    private final IMemberQueryService memberQueryService;
    private final IScrapCommandService scrapCommandService;

    /**
     * 카테고리 생성
     * @throws BaseException 카테고리 생성 제한 개수 초과
     * @throws NoSuchElementException 카테고리의 max sequence를 찾을 수 없을 시
     */
    public Category createCategory(MemberDTO memberDTO, CategoryRequest.CreateCategoryDTO request){
        Member member = memberQueryService.findMember(memberDTO);

        // 카테고리 생성 개수 제한 확인
        long numOfCategory = categoryRepository.countByMemberAndStatus(member, CategoryStatus.ACTIVE);
        boolean isExceedCategoryLimit = numOfCategory >= PolicyData.CATEGORY_CREATE_LIMIT;
        if(isExceedCategoryLimit){
            throw new BaseException(ErrorCode.EXCEED_CATEGORY_CREATE_LIMIT);
        }

        int newCategorySequence = categoryRepository
                .findMaxSequenceByMemberAndStatus(member, CategoryStatus.ACTIVE)
                .orElseThrow(() -> new NoSuchElementException("카테고리의 max sequence를 찾을 수 없음"))
                + 1;

        Category newCategory = CategoryConverter.toEntity(member, request, newCategorySequence);

        categoryRepository.save(newCategory);

        return newCategory;
    }

    /**
     * 기본 카테고리 생성
     */
    public Category createDefaultCategory(Member member){

        Category defaultCategory = CategoryConverter.toEntity(member, PolicyData.DEFAULT_CATEGORY_TITLE, true, 1);

        categoryRepository.save(defaultCategory);

        return defaultCategory;
    }

    /**
     * 카테고리 삭제
     * @throws BaseException 기본 카테고리를 삭제하려 했을 경우
     * @throws BaseException 카테고리의 멤버와 요청멤버가 일치하지 않을 경우
     */
    public void deleteCategory(MemberDTO memberDTO, Long categoryId){
        Member member = memberQueryService.findMember(memberDTO);
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

        category.delete();
    }

    /**
     * 모든 카테고리 삭제하기
     */
    public void deleteAllCategory(MemberDTO memberDTO){ // TODO: 메소드명에 hardDelete 붙이기. memberDTO -> member로 변경하기
        Member member = memberQueryService.findMember(memberDTO);

        categoryRepository.deleteAllByMember(member);
    }

    /** 
     * 카테고리명 수정
     * @throws BaseException 기본 카테고리명을 수정하려 했을 경우
     * @throws BaseException 카테고리의 멤버와 요청멤버가 일치하지 않을 경우
     */
    public Category updateCategoryTitle(MemberDTO memberDTO, Long categoryId, CategoryRequest.UpdateCategoryTitleDTO request){
        Member member = memberQueryService.findMember(memberDTO);
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
     * @throws BaseException 모든 카테고리에 대해 요청을 보내지 않은 경우
     * @throws BaseException 해당하는 카테고리를 찾을 수 없는 경우
     * @return 새로운 순서로 정렬된 카테고리 목록
     */
    public List<Category> updateCategorySequence(MemberDTO memberDTO, CategoryRequest.UpdateCategorySequenceDTO request){
        Member member = memberQueryService.findMember(memberDTO);
        List<Category> categoryList = categoryRepository.findAllByMemberAndStatus(member, CategoryStatus.ACTIVE);

        // 모든 카테고리에 대해 요청했는지 확인
        boolean isRequestCategoryNotAll = request.getCategoryList().size() != categoryList.size();
        if(isRequestCategoryNotAll){
            throw new BaseException(ErrorCode.REQUEST_CATEGORY_COUNT_NOT_ALL);
        }

        // 순서가 변경된 카테고리 id에 차례대로 sequence 순서대로 부여
        Map<Long, Integer> changeSequenceMap = new HashMap<>();
        int newSequence = 1;
        for(Long categoryId : request.getCategoryList()){
            changeSequenceMap.put(categoryId, newSequence);
            newSequence++;
        }

        // 순서 변경
        for(Category category : categoryList){
            if(!changeSequenceMap.containsKey(category.getId())){ // 요청한 카테고리를 못찾은 경우
                throw new BaseException(ErrorCode.CATEGORY_NOT_FOUND);
            }

            category.changeSequence(changeSequenceMap.get(category.getId()));
        }

        Collections.sort(categoryList);
        return categoryList;
    }

}
