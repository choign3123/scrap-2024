package com.example.scrap.web.category;

import com.example.scrap.base.code.ErrorCode;
import com.example.scrap.base.exception.BaseException;
import com.example.scrap.entity.Category;
import com.example.scrap.entity.Member;
import com.example.scrap.entity.Scrap;
import com.example.scrap.web.category.dto.CategoryRequest;
import com.example.scrap.web.member.IMemberService;
import com.example.scrap.web.member.MemberDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class CategoryServiceImpl implements ICategoryService{

    private final CategoryRepository categoryRepository;
    private final IMemberService memberService;

    /**
     * 카테고리 생성
     * @param memberDTO
     * @param request
     * @return 생성된 카테고리
     */
    @Transactional
    public Category createCategory(MemberDTO memberDTO, CategoryRequest.CreateCategoryDTO request){
        Member member = memberService.findMember(memberDTO);

        int newCategorySequence = member.calcNewCategorySequence();

        Category newCategory = Category.builder()
                .title(request.getCategoryTitle())
                .sequence(newCategorySequence)
                .member(member)
                .build();

        categoryRepository.save(newCategory);

        return newCategory;
    }

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
     * 카테고리 삭제
     * @param memberDTO
     * @param categoryId 카테고리 식별자
     */
    @Transactional
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
        Category defaultCategory = findDefaultCategory(member);
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
     * 카테고리명 수정
     * @param memberDTO
     * @param categoryId 카테고리 식별자
     * @param request
     * @return 수정된 카테고리
     */
    @Transactional
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
    @Transactional
    public List<Category> updateCategorySequence(MemberDTO memberDTO, CategoryRequest.UpdateCategorySequenceDTO request){
        Member member = memberService.findMember(memberDTO);

        if(request.getCategoryList().size() != member.getCategoryList().size()){
            throw new BaseException(ErrorCode.REQUEST_CATEGORY_COUNT_NOT_ALL);
        }

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

    private Category findDefaultCategory(Member member){
        return categoryRepository.findByMemberAndIsDefault(member, true)
                .orElseThrow(() -> new BaseException(ErrorCode.CATEGORY_NOT_FOUND));
    }

    public Category findCategory(Long categoryId){
        return categoryRepository.findById(categoryId).get();
    }
}
