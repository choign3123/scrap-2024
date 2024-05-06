package com.example.scrap.converter;

import com.example.scrap.entity.Category;
import com.example.scrap.web.category.dto.CategoryResponse;

import java.util.List;

public class CategoryConverter {

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
}
