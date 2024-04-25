package com.example.scrap.web.category.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

public class CategoryResponse {

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


}
