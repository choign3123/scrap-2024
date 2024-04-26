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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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

        if(category.isIllegalMember(member)){
            throw new BaseException(ErrorCode.CATEGORY_MEMBER_NOT_MATCH);
        }

        // 기본 카테고리는 삭제할 수 없음
        if(category.getIsDefault()){
            throw new BaseException(ErrorCode.NOT_ALLOWED_DEFAULT_CATEGORY_DELETE);
        }

        // 전부다 삭제하는지, 보존해두는지 확인
        if(allowDeleteScrap){
            for(Scrap scrap : category.getScrapList()){
                scrap.toTrash();
            }
        }
        else{
            Category defaultCategory = findDefaultCategory(member);
            // 이렇게 복제된 리스트를 사용하지 않으면, 요소 삭제로 인해 for 문을 다 돌지 못하고 끝나버림.
            List<Scrap> scrapListCopy = new ArrayList<>(category.getScrapList());
            for(Scrap scrap : scrapListCopy){
                scrap.moveCategory(defaultCategory);
            }
        }

        log.info("남은 스크랩: {}", category.getScrapList().size());

        categoryRepository.delete(category);
    }

    private Category findDefaultCategory(Member member){
        return categoryRepository.findByMemberAndIsDefault(member, true)
                .orElseThrow(() -> new BaseException(ErrorCode.CATEGORY_NOT_FOUND));
    }
}
