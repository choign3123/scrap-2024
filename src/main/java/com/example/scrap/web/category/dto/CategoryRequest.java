package com.example.scrap.web.category.dto;

import lombok.Getter;
import javax.validation.constraints.NotBlank;

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
}
