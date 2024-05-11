package com.example.scrap.web.category.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

public class CategoryResponse {

    /**
     * 카테고리 생성 DTO
     */
    @Builder
    @Getter
    public static class CreateCategoryDTO {
        private Long categoryId;
        private String title;
        private int sequence;
    }

    /**
     * 카테고리 전체 조회 DTO
     */
    @Getter
    @Builder
    @JsonPropertyOrder({"categories", "total"})
    public static class GetCategoryListDTO{

        @JsonProperty("categories")
        private List<CategoryDTO> categoryDTOList;

        private int total;

        @Builder
        @Getter
        public static class CategoryDTO{
            private Long categoryId;
            private String categoryTitle;
            private int scrapCnt;
            private int sequence;
        }
    }

    /**
     * 카테고리명 수정 DTO
     */
    @Builder
    @Getter
    public static class UpdateCategoryTitleDTO{

        private String newCategoryTitle;
    }

    /**
     * 카테고리 선택용 조회 DTO
     */
    @Builder
    @Getter
    @JsonPropertyOrder({"categories", "total"})
    public static class GetCategoryListForSelectionDTO {

        @JsonProperty("categories")
        private List<CategoryDTO> categoryDTOList;

        private int total;

        @Builder
        @Getter
        public static class CategoryDTO{
            private Long categoryId;
            private String categoryTitle;
        }
    }

    /**
     * 카테고리 순서 변경 DTO
     */
    @Builder
    @Getter
    @JsonPropertyOrder({"categories", "total"})
    public static class UpdateCategorySequenceDTO {

        @JsonProperty("categories")
        private List<CategoryDTO> categoryDTOList;

        private int total;

        @Builder
        @Getter
        public static class CategoryDTO{
            private Long categoryId;
            private String categoryTitle;
            private int sequence;
        }
    }
}
