package com.example.scrap.web.category;

import com.example.scrap.converter.CategoryConverter;
import com.example.scrap.entity.Category;
import com.example.scrap.entity.Member;
import com.example.scrap.web.category.dto.CategoryRequest;
import com.example.scrap.web.category.dto.CategoryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryServiceImpl implements ICategoryService{

    private final CategoryRepository categoryRepository;

    /**
     * 카테고리 생성
     * @param member
     * @param request
     * @return 생성된 카테고리
     */
    @Transactional
    public Category createCategory(Member member, CategoryRequest.CreateCategoryDTO request){
        int nextCategorySequence = member.getCategoryList().size() + 1;

        Category newCategory = Category.builder()
                .title(request.getCategoryTitle())
                .sequence(nextCategorySequence)
                .member(member)
                .build();

        categoryRepository.save(newCategory);

        return newCategory;
    }

    /**
     * 카테고리 전체 조회
     * @param member
     * @return 전체 카테고리
     */
    public List<Category> getCategoryWholeList(Member member){

        return categoryRepository.findAllByMemberOrderBySequence(member);
    }
}
