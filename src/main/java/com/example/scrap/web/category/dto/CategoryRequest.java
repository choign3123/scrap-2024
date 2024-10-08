package com.example.scrap.web.category.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;

public class CategoryRequest {

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CreateCategoryDTO{

        @Schema(example = "코테 자료")
        @NotBlank
        private String categoryTitle;
    }

    /**
     * 카테고리명 수정
     */
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UpdateCategoryTitleDTO{

        @NotBlank
        private String newCategoryTitle;
    }

    /**
     * 카테고리 순서 변경 DTO
     */
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UpdateCategorySequenceDTO{

        @NotEmpty
        private List<Long> categoryIdList;
    }
}
