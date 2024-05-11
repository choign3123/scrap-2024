package com.example.scrap.web.category.dto;

import com.example.scrap.validation.annotaion.ExistCategories;
import com.example.scrap.validation.annotaion.ExistCategory;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

public class CategoryRequest {

    @Getter
    public static class CreateCategoryDTO{

        @NotBlank
        private String categoryTitle;
    }

    /**
     * 카테고리명 수정
     */
    @Getter
    public static class UpdateCategoryTitleDTO{

        @NotBlank
        private String newCategoryTitle;
    }

    /**
     * 카테고리 순서 변경 DTO
     */
    @Getter
    public static class UpdateCategorySequenceDTO{

        @ExistCategories
        @JsonProperty("categories")
        private List<Long> categoryList;
    }
}
