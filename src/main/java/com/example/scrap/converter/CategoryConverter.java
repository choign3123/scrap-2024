package com.example.scrap.converter;

import com.example.scrap.entity.Category;
import com.example.scrap.entity.Member;
import com.example.scrap.web.category.dto.CategoryRequest;
import com.example.scrap.web.category.dto.CategoryResponse;

import java.util.List;

public class CategoryConverter {

    public static Category toEntity(Member member, String title, boolean isDefault){
        return Category.builder()
                .title(title)
                .isDefault(isDefault)
                .member(member)
                .build();
    }

    public static Category toEntity(Member member, CategoryRequest.CreateCategoryDTO request){
        return Category.builder()
                .isDefault(false)
                .member(member)
                .title(request.getCategoryTitle())
                .build();
    }

    public static CategoryResponse.CreateCategoryDTO toCreateCategoryDTO(Category category){
        return CategoryResponse.CreateCategoryDTO.builder()
                .categoryId(category.getId())
                .title(category.getTitle())
                .sequence(category.getSequence())
                .build();
    }

    public static CategoryResponse.GetCategoryListDTO toGetCategoryListDTO(List<Category> categoryList){
        return CategoryResponse.GetCategoryListDTO.builder()

                .categoryDTOList(
                        categoryList.stream().map(
                                category -> CategoryResponse.GetCategoryListDTO.CategoryDTO.builder()
                                        .categoryId(category.getId())
                                        .categoryTitle(category.getTitle())
                                        .scrapCnt(category.getScrapList().size())
                                        .sequence(category.getSequence())
                                        .isDefault(category.getIsDefault())
                                        .build()
                        ).toList()
                )

                .total(categoryList.size())

                .build();
    }

    public static CategoryResponse.UpdateCategoryTitleDTO toUpdateCategoryTitleDTO(Category category){
        return CategoryResponse.UpdateCategoryTitleDTO.builder()
                .newCategoryTitle(category.getTitle())
                .build();
    }

    public static CategoryResponse.GetCategoryListForSelectionDTO toGetCategoryListForSelectionDTO(List<Category> categoryList){
        return CategoryResponse.GetCategoryListForSelectionDTO.builder()
                .categoryDTOList(
                        categoryList.stream().map(
                                category -> CategoryResponse.GetCategoryListForSelectionDTO.CategoryDTO.builder()
                                        .categoryId(category.getId())
                                        .categoryTitle(category.getTitle())
                                        .build()
                        ).toList()
                )
                .total(categoryList.size())
                .build();
    }

    public static CategoryResponse.UpdateCategorySequenceDTO toUpdateCategorySequenceDTO(List<Category> categoryList){
        return CategoryResponse.UpdateCategorySequenceDTO.builder()
                .categoryDTOList(
                        categoryList.stream().map(
                                category -> CategoryResponse.UpdateCategorySequenceDTO.CategoryDTO.builder()
                                        .categoryId(category.getId())
                                        .categoryTitle(category.getTitle())
                                        .sequence(category.getSequence())
                                        .build()
                        ).toList()
                )
                .total(categoryList.size())
                .build();
    }
}
