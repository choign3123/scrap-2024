package com.example.scrap.web.category.dto;

import com.example.scrap.validation.annotaion.ExistCategories;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import javax.validation.constraints.NotBlank;
import java.util.List;

public class CategoryRequest {

    @Getter
    @AllArgsConstructor
    public static class CreateCategoryDTO{

        @NotBlank
        private String categoryTitle;
    }

    /**
     * 카테고리명 수정
     */
    @Getter
    @AllArgsConstructor
    public static class UpdateCategoryTitleDTO{

        @NotBlank
        private String newCategoryTitle;
    }

    /**
     * 카테고리 순서 변경 DTO
     */
    @Getter
    @AllArgsConstructor
    public static class UpdateCategorySequenceDTO{

        @ExistCategories
        @JsonProperty("categories")
        private List<Long> categoryList;
    }
}
