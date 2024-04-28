package com.example.scrap.web.category;

import com.example.scrap.base.code.ErrorCode;
import com.example.scrap.base.exception.BaseException;
import com.example.scrap.entity.Category;
import com.example.scrap.entity.Member;
import com.example.scrap.web.category.dto.CategoryRequest;
import com.example.scrap.web.member.IMemberService;
import com.example.scrap.web.member.MemberDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
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

        if(category.isIllegalMember(member)){
            throw new BaseException(ErrorCode.CATEGORY_MEMBER_NOT_MATCH);
        }

        // 기본 카테고리명은 수정 불가
        if(category.getIsDefault()){
            throw new BaseException(ErrorCode.NOT_ALLOW_ACCESS_DEFAULT_CATEGORY);
        }

        category.updateTitle(request.getNewCategoryTitle());

        return category;
    }

}
